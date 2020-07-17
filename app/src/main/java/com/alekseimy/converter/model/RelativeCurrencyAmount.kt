package com.alekseimy.converter.model

import java.math.BigDecimal
import java.util.Currency

data class RelativeCurrencyAmount(val currency: Currency, val relativeAmount: BigDecimal)