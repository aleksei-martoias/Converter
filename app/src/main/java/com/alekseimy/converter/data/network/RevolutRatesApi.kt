package com.alekseimy.converter.data.network

import com.alekseimy.converter.data.dto.RatesDTO
import com.alekseimy.converter.data.repository.RatesApi
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.IOException
import java.util.Currency

class RevolutRatesApi(
    private val client: OkHttpClient,
    private val gson: Gson
) : RatesApi {
    private val baseUrl = "https://hiring.revolut.codes/api/android"

    @Throws(IOException::class, IllegalStateException::class)
    override fun requestLatestRates(baseCurrency: Currency): RatesDTO {
        val request = buildRequest(baseCurrency)

        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("$this Unexpected error code=${response.code}")
            }

            response.body
                ?.let { parseResponse(it) }
                ?: throw IllegalStateException("$this Empty body")
        }
    }

    private fun buildRequest(baseCurrency: Currency): Request {
        val requestUrl = "$baseUrl/latest?base=${baseCurrency.currencyCode}"

        return Request.Builder()
            .url(requestUrl)
            .build()
    }

    private fun parseResponse(body: ResponseBody): RatesDTO {
        return gson.fromJson(body.charStream(), RatesDTO::class.java)
    }
}
