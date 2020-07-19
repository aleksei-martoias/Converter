package com.alekseimy.converter.model.converter

import java.math.BigDecimal
import java.util.Currency

data class ConvertedCurrency(val currency: Currency, val relativeAmount: BigDecimal)