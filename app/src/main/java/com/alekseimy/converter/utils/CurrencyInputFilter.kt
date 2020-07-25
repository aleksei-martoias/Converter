package com.alekseimy.converter.utils

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.widget.Toast
import com.alekseimy.converter.R

class CurrencyInputFilter(
    private val context: Context
) : InputFilter {

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        if (source.isNullOrEmpty()) {
            return null
        }

        if (dest.isNullOrEmpty() && source.first() == '.') {
            return "0$source"
        }

        val indexOfDot = dest!!.indexOf('.')
        if (indexOfDot != -1 && indexOfDot < dstart && dest.length - indexOfDot > 2) {
            Toast
                .makeText(context, R.string.currency_input_maximal_dec_part, Toast.LENGTH_SHORT)
                .show()
            return ""
        }

        return null
    }
}