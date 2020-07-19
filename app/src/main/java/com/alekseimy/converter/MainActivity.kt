package com.alekseimy.converter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alekseimy.converter.presentation.ConvertedCurrencyFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportFragmentManager.findFragmentByTag(ConvertedCurrencyFragment.TAG) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, ConvertedCurrencyFragment.newInstance(), ConvertedCurrencyFragment.TAG)
                .commitAllowingStateLoss()
        }
    }
}
