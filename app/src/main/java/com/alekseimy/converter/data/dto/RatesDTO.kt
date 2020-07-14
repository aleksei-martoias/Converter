package com.alekseimy.converter.data.dto

import com.alekseimy.converter.model.Rates
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.Currency

data class RatesDTO(
    @SerializedName("baseCurrency")
    val baseCurrency: String?,
    val rates: Map<String?, String?>?
)

fun RatesDTO.toRates(): Rates? {
    val baseCurrency = baseCurrency?.toCurrency() ?: return null
    val r = HashMap<Currency, BigDecimal>()
    rates?.entries
        ?.filter { it.key != null && it.value != null }
        ?.associateTo(r) { it.key!!.toCurrency() to BigDecimal(it.value) }
        ?: return null

    return Rates(baseCurrency, r)
}

private fun String.toCurrency() = Currency.getInstance(this)