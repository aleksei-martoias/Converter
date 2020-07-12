package com.alekseimy.converter.data.repository

import com.alekseimy.converter.data.dto.RatesDTO
import java.io.IOException
import java.util.Currency

interface RatesApi {

    @Throws(IOException::class, IllegalStateException::class)
    fun requestLatestRates(baseCurrency: Currency): RatesDTO
}