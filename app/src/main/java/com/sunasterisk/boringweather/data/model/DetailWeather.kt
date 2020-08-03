package com.sunasterisk.boringweather.data.model

data class DetailWeather(
    val city: City,
    val dailyWeather: DailyWeather,
    val hourlyWeathers: List<HourlyWeather>
) {
    companion object {
        val default = DetailWeather(City.default, DailyWeather.default, emptyList())
    }
}
