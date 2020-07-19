package com.alekseimy.converter.data.rates.repository

import android.util.Log
import com.alekseimy.converter.data.NetworkStateProvider
import com.alekseimy.converter.data.rates.dto.toRates
import com.alekseimy.converter.model.rates.Rates
import com.alekseimy.converter.model.rates.RatesRepo
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.IOException
import java.util.Currency
import java.util.concurrent.TimeUnit

class RatesRepoImpl(
    private val ratesApi: RatesApi,
    private val networkStateProvider: NetworkStateProvider
) : RatesRepo {

    override fun observeRates(baseCurrency: Currency, withIntervalMs: Long): Flowable<Rates> {
        return Flowable
            .interval(withIntervalMs, TimeUnit.MILLISECONDS)
            .withLatestFrom(
                networkStateProvider.observeNetworkState(),
                BiFunction<Long, NetworkStateProvider.NetworkState, Boolean> {
                        _, state -> state.isConnected
                }
            )
            .filter { it }
            .map { ratesApi.requestLatestRates(baseCurrency) }
            .retry(::canSkipError)
            .filter { ratesDTO ->
                ratesDTO.baseCurrency != null && ratesDTO.rates != null
            }
            .map { it.toRates() }
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