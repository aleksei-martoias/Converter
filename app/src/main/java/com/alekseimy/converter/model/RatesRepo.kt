package com.alekseimy.converter.model

import io.reactivex.rxjava3.core.Flowable
import java.util.Currency

interface RatesRepo {

    fun observeRates(baseCurrency: Currency, withIntervalMs: Long): Flowable<Rates>
}