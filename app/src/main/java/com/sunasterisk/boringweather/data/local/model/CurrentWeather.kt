package com.sunasterisk.boringweather.data.local.model

data class CurrentWeather(
    val currentWeather: HourlyWeather = HourlyWeather(),
    val dailyWeather: DailyWeather = DailyWeather(),
    val todaySummaryWeathers: List<SummaryWeather> = emptyList(),
    val forecastSummaryWeather: List<SummaryWeather> = emptyList()
)
