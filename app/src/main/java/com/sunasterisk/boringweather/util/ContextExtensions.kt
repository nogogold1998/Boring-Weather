package com.sunasterisk.boringweather.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat

fun Context.showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}

fun Context.showSoftInput(view: View, flag: Int = InputMethodManager.SHOW_IMPLICIT) =
    ContextCompat.getSystemService(this, InputMethodManager::class.java)
        ?.showSoftInput(view, flag)
