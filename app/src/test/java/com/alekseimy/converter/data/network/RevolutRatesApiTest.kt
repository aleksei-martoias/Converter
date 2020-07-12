package com.alekseimy.converter.data.network

import com.alekseimy.converter.data.dto.RatesDTO
import com.google.gson.Gson
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.Reader
import java.io.StringReader
import java.util.Currency

class RevolutRatesApiTest {

    private val stubCurrency = spyk(Currency.getInstance("USD"))

    private val responseBody: ResponseBody = mockk()
    private val response: Response = mockk {
        every { body } returns responseBody
        every { close() } just runs
    }
    private val call: Call  = mockk {
        every { execute() } returns response
    }
    private val client: OkHttpClient = mockk()

    private val gson: Gson = mockk()

    private lateinit var revolutRatesApi: RevolutRatesApi

    @Before
    fun setUp() {
        revolutRatesApi = RevolutRatesApi(client, gson)
    }

    @Test
    fun `successfully parse response`() {
        val responsePayload = mockk<StringReader>()
        every { response.isSuccessful } returns true
        every { responseBody.charStream() } returns responsePayload
        val ratesResponseMock: RatesDTO = mockk()
        every { gson.fromJson<RatesDTO>(any<Reader>(), any()) } returns ratesResponseMock
        val requestSlot = slot<Request>()
        every { client.newCall(capture(requestSlot)) } returns call

        val responseData = revolutRatesApi.requestLatestRates(stubCurrency)

        Assert.assertEquals(ratesResponseMock, responseData)
        verify(exactly = 1) {
            client.newCall(any())
            call.execute()
            response.body
            gson.fromJson(any<Reader>(), any())
        }
        verify(exactly = 1) {
            gson.fromJson(responsePayload, RatesDTO::class.java)
        }
        Assert.assertTrue(requestSlot.isCaptured)
        Assert.assertEquals(
            "https://hiring.revolut.codes/api/android/latest?base=USD",
            requestSlot.captured.url.toString()
        )
    }

    @Test
    fun `response with error code`() {
        every { response.isSuccessful } returns false
        every { response.code } returns 404
        val requestSlot = slot<Request>()
        every { client.newCall(capture(requestSlot)) } returns call

        try {
            revolutRatesApi.requestLatestRates(stubCurrency)
        } catch (illegalStateExc: IllegalStateException) {
            // expected
        }

        verify(exactly = 1) {
            client.newCall(any())
            call.execute()
        }
        verify(exactly = 0) {
            response.body
            gson.fromJson(any<Reader>(), any())
        }
        Assert.assertTrue(requestSlot.isCaptured)
        Assert.assertEquals(
            "https://hiring.revolut.codes/api/android/latest?base=USD",
            requestSlot.captured.url.toString()
        )
    }
}