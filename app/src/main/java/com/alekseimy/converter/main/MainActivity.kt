package com.alekseimy.converter.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alekseimy.converter.R
import com.alekseimy.converter.presentation.ConvertedCurrenciesFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportFragmentManager.findFragmentByTag(ConvertedCurrenciesFragment.TAG) == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.fragment_container,
                    ConvertedCurrenciesFragment.newInstance(),
                    ConvertedCurrenciesFragment.TAG
                )
                .commitAllowingStateLoss()
        }
    }
}
