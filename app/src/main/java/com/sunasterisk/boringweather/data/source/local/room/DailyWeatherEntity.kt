package com.sunasterisk.boringweather.data.source.local.room

import androidx.room.Embedded
import androidx.room.Entity
import com.sunasterisk.boringweather.data.model.DailyWeather
import com.sunasterisk.boringweather.data.model.Weather
import com.sunasterisk.boringweather.data.source.local.DailyWeatherTable

@Entity(tableName = DailyWeatherTable.TABLE_NAME, primaryKeys = ["cityId", "dateTime"])
data class DailyWeatherEntity(
    val cityId: Int,
    @Embedded val dailyWeather: DailyWeather,
    @Embedded val weather: Weather = dailyWeather.weathers.firstOrNull() ?: Weather.default
) {
    fun toDailyWeather() = dailyWeather.copy(weathers = listOf(weather))
}
