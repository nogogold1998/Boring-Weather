package com.sunasterisk.boringweather.data.model

import androidx.recyclerview.widget.DiffUtil

data class SummaryWeather(val dt: Long, val temperature: Float, val icon:String?) {
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<SummaryWeather>() {
            override fun areItemsTheSame(
                oldItem: SummaryWeather,
                newItem: SummaryWeather
            ): Boolean = oldItem.dt == newItem.dt

            override fun areContentsTheSame(
                oldItem: SummaryWeather,
                newItem: SummaryWeather
            ): Boolean = oldItem == newItem
        }
    }
}
