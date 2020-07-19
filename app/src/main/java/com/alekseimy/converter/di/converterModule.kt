package com.alekseimy.converter.di

import com.alekseimy.converter.data.NetworkStateProvider
import com.alekseimy.converter.data.rates.network.RevolutRatesApi
import com.alekseimy.converter.data.rates.repository.RatesApi
import com.alekseimy.converter.data.rates.repository.RatesRepoImpl
import com.alekseimy.converter.model.converter.Converter
import com.alekseimy.converter.model.rates.RatesRepo
import com.alekseimy.converter.presentation.ConvertedCurrenciesPresenter
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.koin.dsl.module

val converterModule = module {
    single<RatesApi> { RevolutRatesApi(OkHttpClient(), Gson()) }
    single { NetworkStateProvider(get()) }
    single<RatesRepo> { RatesRepoImpl(get(), get()) }
    single { Converter(get()) }
    factory { ConvertedCurrenciesPresenter(get(), get()) }
}