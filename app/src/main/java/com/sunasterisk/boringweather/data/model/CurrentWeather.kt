package com.sunasterisk.boringweather.data.model

data class CurrentWeather(
    val city: City,
    val currentWeather: HourlyWeather,
    val dailyWeather: DailyWeather,
    val todaySummaryWeathers: List<SummaryWeather>,
    val forecastSummaryWeather: List<SummaryWeather>
) {
    companion object {
        val default =
            CurrentWeather(
                City.default,
                HourlyWeather.default,
                DailyWeather.default,
                emptyList(),
                emptyList()
            )
    }
}
