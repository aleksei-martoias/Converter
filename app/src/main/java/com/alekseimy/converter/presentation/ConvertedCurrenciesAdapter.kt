package com.alekseimy.converter.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.alekseimy.converter.R
import com.alekseimy.converter.model.converter.ConvertedCurrency
import java.math.BigDecimal

class ConvertedCurrenciesAdapter(
    private val context: Context,
    private val listener: Listener
) : RecyclerView.Adapter<ConvertedCurrencyViewHolder>() {

    interface Listener {
        fun onItemClick(rates: ConvertedCurrency)
        fun onAmountChanged(amount: String)
    }

    private val convertedCurrencies: MutableList<ConvertedCurrency> = mutableListOf()

    fun update(updatedConvertedCurrencies: List<ConvertedCurrency>) {
        val result = DiffUtil.calculateDiff(
            Callback(
                ArrayList(convertedCurrencies),
                updatedConvertedCurrencies
            ),
            true
        )
        convertedCurrencies.clear()
        convertedCurrencies.addAll(updatedConvertedCurrencies)
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConvertedCurrencyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exchange_rate_item, parent, false)
        return ConvertedCurrencyViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: ConvertedCurrencyViewHolder, position: Int) {
        val item = convertedCurrencies[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != NO_POSITION) {
                listener.onItemClick(convertedCurrencies[currentPosition])
            }
        }
        holder.listenAmountChanges {
            when (val currentPosition = holder.adapterPosition) {
                NO_POSITION -> {} //ignore
                0 -> listener.onAmountChanged(it)
                else -> {
                    listener.onItemClick(convertedCurrencies[currentPosition])
                    listener.onAmountChanged(it)
                }
            }
        }
    }

    override fun onBindViewHolder(
        holder: ConvertedCurrencyViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            holder.update(payloads.first() as BigDecimal)
        }
    }

    override fun onViewRecycled(holder: ConvertedCurrencyViewHolder) {
        holder.unbing()
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = convertedCurrencies.size

    private class Callback(
        val oldList: List<ConvertedCurrency>,
        val newList: List<ConvertedCurrency>
    ) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].currency.currencyCode == newList[newItemPosition].currency.currencyCode
        }

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].relativeAmount == newList[newItemPosition].relativeAmount
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return newList[newItemPosition].relativeAmount
        }
    }
}