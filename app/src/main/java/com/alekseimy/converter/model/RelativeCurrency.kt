package com.alekseimy.converter.model

import java.math.BigDecimal
import java.util.Currency

data class RelativeCurrency(val currency: Currency, val relativeAmount: BigDecimal)