package com.alekseimy.converter.model

import android.util.Log
import com.alekseimy.converter.data.repository.RatesRepoImpl
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.SerialDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.lang.IllegalStateException
import java.math.BigDecimal
import java.util.Currency
import java.util.LinkedList
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

// TODO Rename to Converter
class Converter(
    private val ratesRepo: RatesRepo,
    private val updateRateInterval: Long = TimeUnit.SECONDS.toMillis(10),
    initialConversionAmount: BigDecimal = BigDecimal.ONE,
    initialConversionTarget: Currency = Currency.getInstance("USD")
) {

    var conversionTarget: Currency = initialConversionTarget
        set(value) {
            modificationLock.withLock {
                if (value.currencyCode == field.currencyCode) {
                    return
                }

                field = value
                reorderRelativeCurrencies()
                sendUpdate()
                resubscribeOnRates()
            }
        }

    // if some field is changed under ReentrantLock it will be synchronized in memory
    // so volatile isn't required for field
    // in addition, all changes will be atomic because are done under lock
    private var refCount = 0
    private val subscriptionLock = ReentrantLock()
    private val modificationLock = ReentrantLock()
    private val relativeCurrencies = LinkedList<RelativeCurrency>()
    private var convertingAmount: BigDecimal = initialConversionAmount
    private var latestFetchedRates: Rates? = null

    private var relativeRatesSubscription = SerialDisposable()
    private val relativeCurrenciesSubject = BehaviorSubject.create<List<RelativeCurrency>>()

    init {
        relativeCurrencies.add(RelativeCurrency(initialConversionTarget, initialConversionAmount))
        relativeCurrenciesSubject.onNext(relativeCurrencies)
    }

    fun updateConversionAmount(amount: BigDecimal) = modificationLock.withLock {
        convertingAmount = amount
        latestFetchedRates?.let {
            if (it.baseCurrency == conversionTarget) {
                convertExceptFirst(it)
                sendUpdate()
            }
        }
    }

    fun observeRelativeCurrencies(): Observable<List<RelativeCurrency>> {
        return relativeCurrenciesSubject
            .doOnSubscribe {
                subscriptionLock.withLock {
                    assert(refCount > -1)
                    ++refCount
                    val disposable = relativeRatesSubscription.get()
                    if (disposable == null || disposable.isDisposed) {
                        assert(refCount == 1)
                        resubscribeOnRates()
                    }
                }
            }
            .doOnDispose {
                subscriptionLock.withLock {
                    assert(refCount > 0)
                    if (--refCount == 0) {
                        relativeRatesSubscription.get().dispose()
                    }
                }
            }
            .map { ArrayList(it) }
    }


    private fun resubscribeOnRates() {
        relativeRatesSubscription.set(
            ratesRepo
                .observeRates(
                    baseCurrency = conversionTarget,
                    withIntervalMs = updateRateInterval
                )
                .onBackpressureLatest()
                .subscribe(
                    {
                        modificationLock.withLock {
                            latestFetchedRates = it
                            intersectSupportedCurrencies(it)
                            convertExceptFirst(it)
                            sendUpdate()
                        }
                    },
                    ::processRatesObservingError
                )
        )
    }

    // comments and spaces looks like smells
    // but one method instead of many helps to make intersection faster
    private fun intersectSupportedCurrencies(fetchedRates: Rates) {
        val fetchedRatesCopy = fetchedRates.copy()

        // any currency which has become unsupported will be removed
        val ratesIterator = relativeCurrencies.iterator()
        val target = ratesIterator.next()
        assert(target.currency == conversionTarget)
        while (ratesIterator.hasNext()) {
            val current = ratesIterator.next()
            fetchedRatesCopy.ratesToBaseCurrency
                .remove(current.currency)
                ?: run { ratesIterator.remove() }
        }

        // any missed currency will be added
        fetchedRatesCopy.ratesToBaseCurrency.forEach { missedCurrency ->
            relativeCurrencies.add(RelativeCurrency(missedCurrency.key, missedCurrency.value))
        }
    }

    private fun sendUpdate() {
        if (relativeCurrencies.isEmpty()) {
            Log.d("$this", "can not send update")
            return
        }

        relativeCurrenciesSubject.onNext(relativeCurrencies)
    }

    private fun convertExceptFirst(fetchedRates: Rates) {
        val iterator = relativeCurrencies.listIterator()
        while (iterator.hasNext()) {
            val current = iterator.next()
            val replacement = fetchedRates.ratesToBaseCurrency[current.currency]
                ?.let { current.copy(relativeAmount = convertingAmount.multiply(it)) }
                ?: current
            iterator.set(replacement)
        }
    }

    private fun processRatesObservingError(thr: Throwable) {
        when (thr) {
            is RuntimeException -> throw thr
            is Error -> throw thr
            else -> Log.e("$this", "rate fetch error", thr)
        }
    }

    private fun reorderRelativeCurrencies() {
        val indexOfNewFirst = relativeCurrencies.indexOfFirst { it.currency == conversionTarget }
        if (indexOfNewFirst == -1) {
            throw IllegalStateException("relativeCurrencies has to contain item with conversionTarget")
        }
        relativeCurrencies.addFirst(relativeCurrencies.removeAt(indexOfNewFirst))
    }
}