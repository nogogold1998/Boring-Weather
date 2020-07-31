package com.sunasterisk.boringweather.ui.detail.model

import com.sunasterisk.boringweather.data.model.DailyWeather
import com.sunasterisk.boringweather.data.model.HourlyWeather
import com.sunasterisk.boringweather.ui.detail.DetailWeatherAdapterViewType

sealed class DetailWeatherAdapterItem<T> {
    abstract val viewType: DetailWeatherAdapterViewType
    abstract val data: T
}

class DailyWeatherItem(override val data: DailyWeather) : DetailWeatherAdapterItem<DailyWeather>() {
    override val viewType = DetailWeatherAdapterViewType.DAILY_WEATHER
}

data class HourlyWeatherItem(override val data: HourlyWeather, var expanded: Boolean = false) :
    DetailWeatherAdapterItem<HourlyWeather>() {
    override val viewType = DetailWeatherAdapterViewType.HOURLY_WEATHER
}
