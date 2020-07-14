package com.alekseimy.converter.data.repository

import android.util.Log
import com.alekseimy.converter.data.dto.toRates
import com.alekseimy.converter.data.network.RevolutRatesApi
import com.alekseimy.converter.model.Rates
import com.alekseimy.converter.model.RatesRepo
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.IOException
import java.util.Currency
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class RatesRepoImpl(
    private val ratesApi: RatesApi = RevolutRatesApi()
): RatesRepo {

    /**
     * [Currency.getInstance] always returns single instance from instances HashMap
     * and uses default equals with links comparison,
     * so it allows to compare keys as fast and precisely as possible
     */
    private val ratesCache = ConcurrentHashMap<Currency, Rates>()

    override fun observeRates(baseCurrency: Currency, withIntervalMs: Long): Flowable<Rates> {
        val networkSource = Flowable
            .interval(withIntervalMs, TimeUnit.MILLISECONDS)
            .map { ratesApi.requestLatestRates(baseCurrency) }
            .retry(::canSkipError)
            .filter { ratesDTO ->
                ratesDTO.baseCurrency?.let { it == baseCurrency.currencyCode } ?: false
            }
            .map { it.toRates() }
            .filter { it != null }
            // TODO check equals
            .doOnNext { ratesCache[it!!.baseCurrency] = it }

        val source = ratesCache[baseCurrency]?.let {
            Flowable.just(it)
                .mergeWith(networkSource)
        } ?: run {
            networkSource
        }

        return source
            .subscribeOn(Schedulers.io())
    }

    private fun canSkipError(thr: Throwable): Boolean {
        Log.e("$this", "error happened when fetching rates", thr)
        return when (thr) {
            is IOException -> true
            is IllegalStateException -> true
            else -> false
        }
    }
}