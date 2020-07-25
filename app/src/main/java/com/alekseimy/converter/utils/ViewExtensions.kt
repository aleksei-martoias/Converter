package com.alekseimy.converter.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.showKeyboard() {
    getInputManager(context)?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    getInputManager(context)?.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

private fun getInputManager(context: Context): InputMethodManager? {
    return context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
}