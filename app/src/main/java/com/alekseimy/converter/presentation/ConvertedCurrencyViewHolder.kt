package com.alekseimy.converter.presentation

import android.content.Context
import android.text.Editable
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alekseimy.converter.R
import com.alekseimy.converter.data.FlagRequestBuilder
import com.alekseimy.converter.model.converter.ConvertedCurrency
import com.alekseimy.converter.utils.CurrencyInputFilter
import com.alekseimy.converter.utils.hideKeyboard
import com.alekseimy.converter.utils.showKeyboard
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
        amountInput.filters = arrayOf(CurrencyInputFilter(context), LengthFilter(INPUT_LENGTH_LIMIT))
        amountInput.setOnEditorActionListener { view, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN || keyCode == KeyEvent.KEYCODE_CALL) {
                view.hideKeyboard()
                view.clearFocus()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    fun unbing() {
        releaseInputField()
    }

    fun bind(convertedCurrency: ConvertedCurrency) {
        currencyCode.text = convertedCurrency.currency.currencyCode
        currencyDescription.text = convertedCurrency.currency.displayName
        updateAmount(convertedCurrency.relativeAmount.toText())
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
        if (!amountInput.hasFocus()) {
            updateAmount(relativeAmount.toText())
        }
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

    fun startInput() {
        amountInput.requestFocus()
        amountInput.showKeyboard()
    }

    private fun updateAmount(text: String) {
        val editableText = amountInput.editableText
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
}