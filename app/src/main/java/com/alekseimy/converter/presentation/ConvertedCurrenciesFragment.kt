package com.alekseimy.converter.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alekseimy.converter.R
import org.koin.android.ext.android.inject

class ConvertedCurrenciesFragment : Fragment() {

    private lateinit var convertedCurrenciesView: ConvertedCurrenciesView
    private val convertedCurrenciesPresenter: ConvertedCurrenciesPresenter by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exchange_rates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        convertedCurrenciesView =
            ConvertedCurrenciesView(
                view,
                requireContext()
            )
    }

    override fun onStart() {
        super.onStart()
        // in onStart to allow updates in split screen
        convertedCurrenciesPresenter.attach(convertedCurrenciesView)
        convertedCurrenciesPresenter.start()
    }

    override fun onStop() {
        convertedCurrenciesPresenter.release()
        convertedCurrenciesPresenter.detach()
        super.onStop()
    }

    companion object {
        const val TAG = "ratesFragmentTag"

        fun newInstance() =
            ConvertedCurrenciesFragment()
    }
}
