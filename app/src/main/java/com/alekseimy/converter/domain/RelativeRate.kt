package com.alekseimy.converter.domain

import java.math.BigDecimal
import java.util.Currency

data class RelativeRate(
    val currency: Currency,
    val relativelyToCurrency: Currency,
    val rate: BigDecimal
)