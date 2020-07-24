package com.alekseimy.converter.data

import java.util.Currency

class FlagRequestBuilder private constructor() {

    private var flagTheme: FlagTheme = FlagTheme.FLAT
    private var size: FlagSize = FlagSize.LARGE
    private lateinit var countyCode: String

    enum class FlagTheme(val code: String) {
        FLAT("flat"),
        SHINY("shiny")
    }

    enum class FlagSize(val code: String) {
        SMALL("16"),
        MEDIUM("32"),
        LARGE("64")
    }

    fun setCounty(currency: Currency): FlagRequestBuilder {
        countyCode = currency.currencyCode.substring(0, 2)
        return this
    }

    fun setSize(size: FlagSize): FlagRequestBuilder {
        this.size = size
        return this
    }

    fun setTheme(theme: FlagTheme): FlagRequestBuilder {
        flagTheme = theme
        return this
    }

    fun build(): String = "$baseUrl$countyCode/${flagTheme.code}/${size.code}.png"

    companion object {

        private const val baseUrl: String = "https://www.countryflags.io/"

        fun newBuilder(): FlagRequestBuilder = FlagRequestBuilder()
    }
}