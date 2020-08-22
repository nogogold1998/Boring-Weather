package com.sunasterisk.boringweather.util

import androidx.core.view.ScrollingView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

val ScrollingView.verticalScrollProgress: Float
    get() {
        val offset = computeVerticalScrollOffset()
        val extent = computeVerticalScrollExtent()
        val range = computeVerticalScrollRange()

        return offset / (range - extent).toFloat()
    }

val RecyclerView.lastCompletelyVisibleItemPosition: Int
    get() = (layoutManager as? LinearLayoutManager)?.findLastCompletelyVisibleItemPosition() ?: 0

val RecyclerView.firstCompletelyVisibleItemPosition: Int
    get() = (layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition() ?: 0

// ugly code
val RecyclerView.centerItemPosition: Int
    get() = arrayOf(firstCompletelyVisibleItemPosition + lastCompletelyVisibleItemPosition)
        .filter { it > 0 }
        .average()
        .roundToInt()

fun RecyclerView.setupDefaultItemDecoration() {
    (layoutManager as? LinearLayoutManager)?.orientation
        ?.let { addItemDecoration(DividerItemDecoration(context, it)) }
}
