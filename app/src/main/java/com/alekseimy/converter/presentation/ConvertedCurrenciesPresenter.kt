package com.alekseimy.converter.presentation

import android.util.Log
import android.widget.Toast
import com.alekseimy.converter.data.NetworkStateProvider
import com.alekseimy.converter.model.converter.Converter
import com.alekseimy.converter.model.converter.ConvertedCurrency
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.SerialDisposable
import java.lang.RuntimeException
import java.math.BigDecimal

class ConvertedCurrenciesPresenter(
    private val exchangeRates: Converter,
    private val networkStateProvider: NetworkStateProvider
) {

    private var view: ConvertedCurrenciesView? = null
    private val ratesUpdateSubscription = SerialDisposable()
    private val connectionSubscription = SerialDisposable()

    fun attach(view: ConvertedCurrenciesView) {
        this.view = view
        view.setActions(
            object :
                ConvertedCurrenciesView.Actions {
                override fun onRateClick(rates: ConvertedCurrency) {
                    exchangeRates.conversionTarget = rates
                }

                override fun onAmountChanged(amount: BigDecimal) {
                    exchangeRates.updateConversionAmount(amount)
                }
            }
        )
    }

    fun start() {
        ratesUpdateSubscription.set(
            exchangeRates.observeConvertedCurrencies()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { view?.showRates(it) },
                    ::processError
                )
        )
        connectionSubscription.set(
            networkStateProvider
                .observeNetworkState()
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    ::processNetworkState,
                    ::processError
                )
        )
    }

    fun release() {
        ratesUpdateSubscription.get().dispose()
    }

    fun detach() {
        view = null
    }

    private fun processNetworkState(state: NetworkStateProvider.NetworkState) {
        if (!state.isConnected) {
            view?.showNetworkError()
        } else {
            view?.hideNetworkError()
        }
    }

    private fun processError(thr: Throwable) {
        when (thr) {
            is RuntimeException -> throw thr
            else -> Log.e("$this", "have not got update", thr)
        }
    }
}