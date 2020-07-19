package com.alekseimy.converter.data.rates.repository

import com.alekseimy.converter.data.rates.dto.RatesDTO
import java.io.IOException
import java.util.Currency

interface RatesApi {

    @Throws(IOException::class, IllegalStateException::class)
    fun requestLatestRates(baseCurrency: Currency): RatesDTO
}