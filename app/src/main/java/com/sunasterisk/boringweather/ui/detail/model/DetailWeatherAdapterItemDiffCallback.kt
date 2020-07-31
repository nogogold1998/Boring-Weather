package com.sunasterisk.boringweather.ui.detail.model

import androidx.recyclerview.widget.DiffUtil

class DetailWeatherAdapterItemDiffCallback : DiffUtil.ItemCallback<DetailWeatherAdapterItem<*>>() {
    override fun areItemsTheSame(
        oldItem: DetailWeatherAdapterItem<*>,
        newItem: DetailWeatherAdapterItem<*>
    ): Boolean = when (oldItem) {
        is DailyWeatherItem ->
            newItem is DailyWeatherItem && oldItem.data.dateTime == newItem.data.dateTime
        is HourlyWeatherItem ->
            newItem is HourlyWeatherItem && oldItem.data.dateTime == newItem.data.dateTime
    }

    override fun areContentsTheSame(
        oldItem: DetailWeatherAdapterItem<*>,
        newItem: DetailWeatherAdapterItem<*>
    ): Boolean = when (oldItem) {
        is DailyWeatherItem -> newItem is DailyWeatherItem && oldItem.data == newItem.data
        is HourlyWeatherItem -> newItem is HourlyWeatherItem && oldItem.data == newItem.data
    }
}
