package com.alekseimy.converter.data.rates.dto

import com.alekseimy.converter.model.rates.Rates
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.Currency

data class RatesDTO(
    @SerializedName("baseCurrency")
    val baseCurrency: String?,
    val rates: Map<String?, String?>?
)

fun RatesDTO.toRates(): Rates {
    val baseCurrency = baseCurrency!!.toCurrency()
    val rates = HashMap<Currency, BigDecimal>()
    this.rates!!.entries
        .filter { it.key != null && it.value != null }
        .associateTo(rates) { it.key!!.toCurrency() to BigDecimal(it.value) }

    return Rates(baseCurrency, rates)
}

private fun String.toCurrency() = Currency.getInstance(this)