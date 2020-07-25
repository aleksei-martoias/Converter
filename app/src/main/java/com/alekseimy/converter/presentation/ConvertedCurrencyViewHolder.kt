package com.alekseimy.converter.presentation

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.Spanned
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.recyclerview.widget.RecyclerView
import com.alekseimy.converter.R
import com.alekseimy.converter.data.FlagRequestBuilder
import com.alekseimy.converter.model.converter.ConvertedCurrency
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.exchange_rate_item.view.*
import java.math.BigDecimal

private const val INPUT_LENGTH_LIMIT = 14

class ConvertedCurrencyViewHolder(
    private val context: Context,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val currencyFlagImg: ImageView = itemView.currency_img


    private val currencyCode: TextView = itemView.currency_code
    private val currencyDescription: TextView = itemView.currency_description

    private val amountInput: EditText = itemView.amount_input
    private var canListenUserInput = true
    private var amountInputChangesListener: TextWatcher? = null

    init {
        amountInput.filters = arrayOf(CurrencyInputFilter(), LengthFilter(INPUT_LENGTH_LIMIT))
    }

    fun unbing() {
        releaseInputField()
    }

    fun bind(convertedCurrency: ConvertedCurrency) {
        currencyCode.text = convertedCurrency.currency.currencyCode
        currencyDescription.text = convertedCurrency.currency.displayName
        amountInput.updateText(convertedCurrency.relativeAmount.toText())

        Glide.with(context)
            .load(
                FlagRequestBuilder.newBuilder()
                    .setCounty(convertedCurrency.currency)
                    .build()
            )
            .placeholder(R.drawable.ic_flag_placeholder_40dp)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(currencyFlagImg)
    }

    fun update(relativeAmount: BigDecimal) {
        amountInput.updateText(relativeAmount.toText())
    }

    fun listenAmountChanges(listener: (String) -> Unit) {
        releaseInputField()
        amountInputChangesListener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (canListenUserInput) {
                    s?.let { listener(it.toString()) }
                }
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                //ignore
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // ignore
            }
        }
        amountInput.addTextChangedListener(amountInputChangesListener)
    }

    private fun EditText.updateText(text: String) {
        if (editableText.toString() == text) {
            return
        }

        canListenUserInput = false
        editableText.clear()
        editableText.append(text)
        canListenUserInput = true
    }

    private fun releaseInputField() {
        amountInputChangesListener?.let { amountInput.removeTextChangedListener(it) }
    }

    private fun BigDecimal.toText(): String {
        return if (scale() == 0) {
            toPlainString()
        } else {
            "%.2f".format(this)
        }
    }

    private inner class CurrencyInputFilter : InputFilter {

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
                    .makeText(context, R.string.currency_input_maximal_dec_part, LENGTH_SHORT)
                    .show()
                return ""
            }

            return null
        }
    }
}