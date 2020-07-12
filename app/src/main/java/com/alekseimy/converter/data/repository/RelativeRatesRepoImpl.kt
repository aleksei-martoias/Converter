package com.alekseimy.converter.data.repository

import com.alekseimy.converter.data.dto.toRates
import com.alekseimy.converter.data.network.RevolutRatesApi
import com.alekseimy.converter.domain.RelativeRate
import com.alekseimy.converter.domain.RelativeRatesRepo
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.IOException
import java.util.Currency
import java.util.concurrent.TimeUnit

class RelativeRatesRepoImpl(
    private val ratesApi: RevolutRatesApi
): RelativeRatesRepo {

    override fun getRates(baseCurrency: Currency, withIntervalMs: Long): Observable<List<RelativeRate>> {
        return Observable
            .interval(withIntervalMs, TimeUnit.MILLISECONDS)
            // TODO ("observe connection")
            .map { ratesApi.requestLatestRates(baseCurrency) }
            .retry(::canSkipError)
            .filter { it.baseCurrency != null}
            .map { it.toRates() }
            .filter { it.isNotEmpty() }
            .subscribeOn(Schedulers.io())
    }

    private fun canSkipError(thr: Throwable): Boolean {
        return when (thr) {
            is IOException -> true
            is IllegalStateException -> true
            else -> false
        }
    }
}