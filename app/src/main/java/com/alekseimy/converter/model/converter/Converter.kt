package com.alekseimy.converter.model.converter

import android.util.Log
import com.alekseimy.converter.model.rates.Rates
import com.alekseimy.converter.model.rates.RatesRepo
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

class Converter(
    private val ratesRepo: RatesRepo,
    private val updateRateInterval: Long = TimeUnit.SECONDS.toMillis(5),
    initialConversionAmount: BigDecimal = BigDecimal.ONE,
    initialConversionTarget: Currency = Currency.getInstance("USD")
) {


    /**
     * [Currency.getInstance] always returns single instance from instances HashMap
     * and uses default equals with links comparison,
     * so it allows to compare keys as fast and precisely as possible
     */
    var conversionTarget: ConvertedCurrency =
        ConvertedCurrency(
            initialConversionTarget,
            initialConversionAmount
        )
        set(value) {
            modificationLock.withLock {
                if (value.currency == field.currency) {
                    return
                }

                field = value
                convertingAmount = value.relativeAmount
                reorderRelativeCurrencies()
                sendUpdate()
                resubscribeOnRates()
            }
        }

    // if some field is changed under lock it will be synchronized in memory
    // so volatile isn't required for field
    // in addition, all changes will be atomic because are done under lock
    private var refCount = 0
    private val subscriptionLock = ReentrantLock()
    private val modificationLock = ReentrantLock()
    private val convertedCurrencies = LinkedList<ConvertedCurrency>()
    private var convertingAmount: BigDecimal = initialConversionAmount
    private var latestFetchedRates: Rates? = null

    private var relativeRatesSubscription = SerialDisposable()
    private val relativeCurrenciesSubject = BehaviorSubject.create<List<ConvertedCurrency>>()

    init {
        convertedCurrencies.add(
            ConvertedCurrency(
                initialConversionTarget,
                initialConversionAmount
            )
        )
        relativeCurrenciesSubject.onNext(convertedCurrencies)
    }

    fun updateConversionAmount(amount: BigDecimal) = modificationLock.withLock {
        convertingAmount = amount
        latestFetchedRates?.let {
            if (it.baseCurrency == conversionTarget.currency) {
                convertExceptFirst(it)
                sendUpdate()
            }
        }
    }

    fun observeConvertedCurrencies(): Observable<List<ConvertedCurrency>> {
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
                    baseCurrency = conversionTarget.currency,
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
        val ratesIterator = convertedCurrencies.iterator()
        val target = ratesIterator.next()
        assert(target.currency == conversionTarget.currency )
        while (ratesIterator.hasNext()) {
            val current = ratesIterator.next()
            fetchedRatesCopy.ratesToBaseCurrency
                .remove(current.currency)
                ?: run { ratesIterator.remove() }
        }

        // any missed currency will be added
        fetchedRatesCopy.ratesToBaseCurrency.forEach { missedCurrency ->
            convertedCurrencies.add(
                ConvertedCurrency(
                    missedCurrency.key,
                    missedCurrency.value
                )
            )
        }
    }

    private fun sendUpdate() {
        if (convertedCurrencies.isEmpty()) {
            Log.d("$this", "can not send update")
            return
        }

        relativeCurrenciesSubject.onNext(convertedCurrencies)
    }

    private fun convertExceptFirst(fetchedRates: Rates) {
        val iterator = convertedCurrencies.listIterator()
        while (iterator.hasNext()) {
            val current = iterator.next()
            val replacement = fetchedRates.ratesToBaseCurrency[current.currency]
                ?.let { current.copy(relativeAmount = convertingAmount.multiply(it)) }
                ?: current.copy(relativeAmount = convertingAmount.multiply(BigDecimal.ONE))
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
        val indexOfNewFirst = convertedCurrencies.indexOfFirst { it.currency == conversionTarget.currency }
        if (indexOfNewFirst == -1) {
            throw IllegalStateException("relativeCurrencies has to contain item with conversionTarget")
        }
        convertedCurrencies.addFirst(convertedCurrencies.removeAt(indexOfNewFirst))
    }
}