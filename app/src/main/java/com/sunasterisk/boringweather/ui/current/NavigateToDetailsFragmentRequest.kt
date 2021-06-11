package com.sunasterisk.boringweather.ui.current

sealed class CurrentFragmentNavigationRequest

data class NavigateToDetailsFragmentRequest(
    val cityId: Int,
    val dailyWeatherDateTime: Long,
    val focusHourlyWeatherDateTime: Long? = null,
) : CurrentFragmentNavigationRequest()
