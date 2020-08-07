package com.sunasterisk.boringweather.ui.detail.model

data class LoadDetailWeatherRequest (
    val cityId: Int,
    val dailyWeatherDateTime: Long,
    val refresh: Boolean = false
)
