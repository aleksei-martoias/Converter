package com.alekseimy.converter.model.rates

import java.math.BigDecimal
import java.util.Currency

data class Rates(
    val baseCurrency: Currency,
    val ratesToBaseCurrency: HashMap<Currency, BigDecimal>
) {
    
    fun copy(): Rates {
        return Rates(
            baseCurrency,
            HashMap(ratesToBaseCurrency)
        )
    }
}