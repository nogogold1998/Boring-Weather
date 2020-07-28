package com.sunasterisk.boringweather.util

import androidx.core.view.ScrollingView

val ScrollingView.verticalScrollProgress: Float
    get() {
        val offset = computeVerticalScrollOffset()
        val extent = computeVerticalScrollExtent()
        val range = computeVerticalScrollRange()

        return offset / (range - extent).toFloat()
    }

val ScrollingView.horizontalScrollProgress: Float
    get() {
        val offset = computeHorizontalScrollOffset()
        val extent = computeHorizontalScrollExtent()
        val range = computeHorizontalScrollRange()

        return offset / (range - extent).toFloat()
    }
