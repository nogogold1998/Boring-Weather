package com.sunasterisk.boringweather.util

import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

// DISCLAIMER: not written by me
// source: https://stackoverflow.com/questions/42522739/kotlin-check-if-lazy-val-has-been-initialised
val KProperty0<*>.isLazyInitialized: Boolean
    get() {
        if (this !is Lazy<*>) return true
        val originalAccessLevel = isAccessible
        isAccessible = true
        val isLazyInitialized = (getDelegate() as Lazy<*>).isInitialized()
        isAccessible = originalAccessLevel
        return isLazyInitialized
    }
