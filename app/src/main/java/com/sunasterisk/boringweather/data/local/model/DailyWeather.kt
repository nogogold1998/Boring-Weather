package com.sunasterisk.boringweather.data.local.model

import com.sunasterisk.boringweather.util.getOrElse
import com.sunasterisk.boringweather.util.getOrNull
import com.sunasterisk.boringweather.util.map
import org.json.JSONArray
import org.json.JSONObject

data class DailyWeather(
    val dateTime: Long = 0,
    val sunrise: Long = 0,
    val sunset: Long = 0,
    val temperature: Temperature = Temperature(),
    val pressure: Int = 0,
    val humidity: Int = 0,
    val dewPoint: Float = 0f,
    val windSpeed: Float = 0f,
    val windDegree: Int = 0,
    val weathers: List<Weather>,
    val clouds: Int = 0,
    val uvIndex: Float = 0f
) {

    constructor(jsonObject: JSONObject) : this(
        jsonObject.getOrElse(DT, 0L),
        jsonObject.getOrElse(SUNRISE, 0L),
        jsonObject.getOrElse(SUNSET, 0L),
        jsonObject.getOrNull<JSONObject>(TEMP)?.let(::Temperature) ?: Temperature(),
        jsonObject.getOrElse(PRESSURE, 0),
        jsonObject.getOrElse(HUMIDITY, 0),
        jsonObject.getOrElse(DEW_POINT, 0f),
        jsonObject.getOrElse(WIND_SPEED, 0f),
        jsonObject.getOrElse(WIND_DEGREES, 0),
        jsonObject.getOrNull<JSONArray>(WEATHER)?.map(::Weather) ?: emptyList(),
        jsonObject.getOrElse(CLOUDS, 0),
        jsonObject.getOrElse(UVI, 0f)
    )

    companion object {

        private const val DT = "dt"
        private const val SUNRISE = "sunrise"
        private const val SUNSET = "sunset"
        private const val TEMP = "temp"
        private const val PRESSURE = "pressure"
        private const val HUMIDITY = "humidity"
        private const val CLOUDS = "clouds"
        private const val WEATHER = "weather"
        private const val UVI = "uvi"
        private const val DEW_POINT = "dew_point"
        private const val WIND_SPEED = "wind_speed"
        private const val WIND_DEGREES = "wind_deg"
    }
}
