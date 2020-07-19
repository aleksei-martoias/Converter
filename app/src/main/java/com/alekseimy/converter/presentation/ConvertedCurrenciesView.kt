package com.alekseimy.converter.presentation

import android.content.Context
import android.view.View
import android.view.ViewParent
import android.widget.FrameLayout
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alekseimy.converter.R
import com.alekseimy.converter.model.converter.ConvertedCurrency
import kotlinx.android.synthetic.main.fragment_exchange_rates.view.*
import java.lang.NumberFormatException
import java.math.BigDecimal

class ConvertedCurrenciesView(
    view: View,
    context: Context
) {

    interface Actions {
        fun onRateClick(rates: ConvertedCurrency)
        fun onAmountChanged(amount: BigDecimal)
    }

    private val networkStatus: FrameLayout = view.network_status
    private val ratesList: RecyclerView = view.rates_list
    private val adapter: ConvertedCurrenciesAdapter =
        ConvertedCurrenciesAdapter(
            context,
            object :
                ConvertedCurrenciesAdapter.Listener {
                override fun onItemClick(rates: ConvertedCurrency) {
                    actions?.onRateClick(rates)
                }

                override fun onAmountChanged(amount: String) {
                    try {
                        actions?.onAmountChanged(BigDecimal(amount))
                    } catch (amountInputError: NumberFormatException) {
                        // show error
                    }
                }
            }
        )

    private var actions: Actions? = null

    init {
        ratesList.layoutManager = LinearLayoutManager(view.context)
        ratesList.adapter = adapter
        ratesList.setHasFixedSize(true)
    }

    fun showRates(rates: List<ConvertedCurrency>) {
        adapter.update(rates)
        ratesList.scrollToPosition(0)
    }

    fun setActions(actions: Actions) {
        this.actions = actions
    }

    fun showNetworkError() {
        networkStatus.visibility = View.VISIBLE
    }

    fun hideNetworkError() {
        networkStatus.visibility = View.GONE
    }
}