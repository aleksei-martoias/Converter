package com.alekseimy.converter.domain

import io.reactivex.rxjava3.core.Observable
import java.util.Currency

interface RelativeRatesRepo {

    fun getRates(baseCurrency: Currency, withIntervalMs: Long): Observable<List<RelativeRate>>
}