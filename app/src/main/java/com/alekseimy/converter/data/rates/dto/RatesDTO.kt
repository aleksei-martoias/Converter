package com.alekseimy.converter.data.rates.dto

import com.google.gson.annotations.SerializedName

data class RatesDTO(
    @SerializedName("baseCurrency")
    val baseCurrency: String?,
    val rates: Map<String?, String?>
)