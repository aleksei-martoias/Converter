package com.alekseimy.converter

import android.app.Application
import com.alekseimy.converter.di.converterModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Converter : Application() {

    override fun onCreate() {
        super.onCreate()
        setUpDi()
    }

    private fun setUpDi() {
        startKoin {
            androidContext(this@Converter)
            modules(converterModule)
        }
    }
}