package com.alekseimy.converter.data.dto

import com.alekseimy.converter.domain.RelativeRate
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.Currency

data class RatesDTO(
    @SerializedName("baseCurrency")
    val baseCurrency: String?,
    val rates: Map<String?, String?>?
)

fun RatesDTO.toRates(): List<RelativeRate> {
    val baseCurrency = baseCurrency
        ?.let{
            Currency.getInstance(baseCurrency)
        }
        ?: return emptyList()

    return rates
        ?.filter { it.key != null && it.value != null }
        ?.map {
            RelativeRate(
                currency = Currency.getInstance(it.key!!),
                relativelyToCurrency = baseCurrency,
                rate = BigDecimal(it.value!!)
            )
        }?.toList() ?: emptyList()
}