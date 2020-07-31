package com.sunasterisk.boringweather.ui.detail

import androidx.annotation.LayoutRes
import com.sunasterisk.boringweather.R

enum class DetailWeatherAdapterViewType(@LayoutRes val layoutRes: Int) {
    DAILY_WEATHER(R.layout.item_weather_daily_header),
    HOURLY_WEATHER(R.layout.item_weather_hourly_expandable)
}
