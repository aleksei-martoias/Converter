package com.alekseimy.converter.presentation

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alekseimy.converter.model.converter.ConvertedCurrency
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import kotlinx.android.synthetic.main.exchange_rate_item.view.*
import java.math.BigDecimal

class ConvertedCurrencyViewHolder(
    private val context: Context,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val currencyFlagImg: ImageView = itemView.currency_img
    private val currencyCode: TextView = itemView.currency_code
    private val currencyDescription: TextView = itemView.currency_description
    private val amountInput: EditText = itemView.amount_input

    private var canListenUserInput = true

    fun bind(convertedCurrency: ConvertedCurrency) {
        // load flag
        currencyCode.text = convertedCurrency.currency.currencyCode
        currencyDescription.text = convertedCurrency.currency.displayName
        amountInput.updateText(convertedCurrency.relativeAmount.toText())

        Glide.with(context)
            .load("https://www.countryflags.io/${convertedCurrency.currency.currencyCode.substring(0, 2)}/flat/64.png")
            .transform(CircleCrop())
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(currencyFlagImg)
    }

    fun update(relativeAmount: BigDecimal) {
        amountInput.updateText(relativeAmount.toText())
    }

    fun listenAmountChanges(listener: (String) -> Unit) {
        amountInput.addTextChangedListener(
            object : TextWatcher {
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
        )
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

    private fun BigDecimal.toText(): String {
        return if (scale() == 0) {
            toPlainString()
        } else {
            "%.2f".format(this)
        }
    }
}