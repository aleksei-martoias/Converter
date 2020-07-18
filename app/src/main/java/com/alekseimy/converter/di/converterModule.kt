package com.alekseimy.converter.di

import com.alekseimy.converter.data.network.RevolutRatesApi
import com.alekseimy.converter.data.repository.RatesRepoImpl
import com.alekseimy.converter.model.Converter
import org.koin.dsl.module

val converterModule = module {
    single { RevolutRatesApi() }
    single { RatesRepoImpl(get()) }
    single { Converter(get()) }
}