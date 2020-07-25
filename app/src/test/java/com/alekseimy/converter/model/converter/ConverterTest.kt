package com.alekseimy.converter.model.converter

import com.alekseimy.converter.model.rates.Rates
import com.alekseimy.converter.model.rates.RatesRepo
import com.alekseimy.converter.rules.OverrideSchedulers
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.util.Currency

class ConverterTest {

    @get:Rule
    val overrideSchedulers = OverrideSchedulers()

    private val currencies: MutableMap<String, Currency> = HashMap()

    private val ratesRepoMock = mockk<RatesRepo>()

    private val initialConversionTarget = "USD"
    private lateinit var converter: Converter

    @Before
    fun setUp() {
        converter = Converter(
            ratesRepo = ratesRepoMock,
            initialConversionTarget = mockCurrency(initialConversionTarget)
        )
    }

    @Test
    fun `conversion target becomes first after has been updated`() {
        val targetCurrency = mockCurrency("EUR")
        val newConversionTarget = mockConversionTarget(targetCurrency)
        val ratesSubject = mockRatesRepo()
        val rates = arrayOf(Pair(targetCurrency, BigDecimal.ONE))
        ratesSubject.onNext(mockRates(initialConversionTarget, rates))

        val testObserver = converter.observeConvertedCurrencies().test()
        converter.conversionTarget = newConversionTarget

        Assert.assertEquals(testObserver.values()[0].first().currency, currencies[initialConversionTarget])
        Assert.assertEquals(testObserver.values()[1].first().currency, targetCurrency)
        testObserver.dispose()
    }

    @Test
    fun `currencies will be converted after update of conversion value`() {
        val ratesSubject = mockRatesRepo()
        val conversionValue = BigDecimal.TEN
        val rates = arrayOf(Pair(mockCurrency("EUR"), BigDecimal.ONE))
        ratesSubject.onNext(mockRates(initialConversionTarget, rates))

        val testObserver = converter.observeConvertedCurrencies().test()
        converter.updateConversionAmount(conversionValue)

        checkCurrenciesConverted(testObserver.values().last(), rates, conversionValue)
        testObserver.dispose()
    }

    private fun mockConversionTarget(
        targetCurrency: Currency,
        amount: BigDecimal = BigDecimal.ONE
    ): ConvertedCurrency {
        return mockk {
            every { currency } returns targetCurrency
            every { relativeAmount } returns amount
        }
    }

    private fun mockRatesRepo(): BehaviorSubject<Rates> {
        val subject = BehaviorSubject.create<Rates>()
        every { ratesRepoMock.observeRates(any(), any()) } returns subject.toFlowable(BackpressureStrategy.LATEST)
        return subject
    }

    private fun mockRates(baseCurrencyCode: String, ratesForCurrencies: Array<Pair<Currency, BigDecimal>>): Rates {
        val baseCurrencyMock = mockCurrency(baseCurrencyCode)
        val ratesToBaseCurrency = HashMap<Currency, BigDecimal>()
        ratesForCurrencies.associateTo(ratesToBaseCurrency) { it }
        return Rates(baseCurrencyMock, ratesToBaseCurrency)
    }

    private fun mockCurrency(code: String): Currency {
        return currencies.getOrPut(code) {
            mockk {
                every { currencyCode } returns code
            }
        }
    }

    private fun checkCurrenciesConverted(
        convertedCurrencies: List<ConvertedCurrency>,
        rates: Array<Pair<Currency, BigDecimal>>,
        conversionValue: BigDecimal
    ) {
        convertedCurrencies.forEach { convertedCurrency ->
            val expected = rates.find { it.first == convertedCurrency.currency }
                ?.let { it.second * conversionValue }
                ?: convertedCurrency.relativeAmount
            Assert.assertTrue(convertedCurrency.relativeAmount == expected)
        }
    }
}