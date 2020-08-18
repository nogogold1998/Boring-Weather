package com.sunasterisk.boringweather.data.source.local.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.HourlyWeather
import com.sunasterisk.boringweather.data.model.Weather
import com.sunasterisk.boringweather.data.source.local.HourlyWeatherTable

@Entity(
    tableName = HourlyWeatherTable.TABLE_NAME,
    primaryKeys = ["cityId", "dateTime"],
    foreignKeys = [ForeignKey(
        entity = City::class,
        parentColumns = ["id"],
        childColumns = ["cityId"]
    )]
)
data class HourlyWeatherEntity(
    val cityId: Int,
    @Embedded val hourlyWeather: HourlyWeather,
    @Embedded val weather: Weather = hourlyWeather.weathers.firstOrNull() ?: Weather.default
) {
    fun toHourlyWeather() = hourlyWeather.copy(weathers = listOf(weather))
}
