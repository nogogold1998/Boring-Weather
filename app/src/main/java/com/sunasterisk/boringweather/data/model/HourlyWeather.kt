package com.sunasterisk.boringweather.data.model

import com.sunasterisk.boringweather.util.getOrElse
import com.sunasterisk.boringweather.util.getOrNull
import com.sunasterisk.boringweather.util.map
import org.json.JSONArray
import org.json.JSONObject

data class HourlyWeather(
    val dateTime: Long = 0,
    val temperature: Float = 0f,
    val feelsLike: Float = 0f,
    val pressure: Int = 0,
    val humidity: Int = 0,
    val dewPoint: Float = 0f,
    val clouds: Int = 0,
    val windSpeed: Float = 0f,
    val windDegrees: Int = 0,
    val weathers: List<Weather> = emptyList(),
    val visibility: Int? = null,
    val windGust: Float? = null,
    val rain: Volume? = null,
    val snow: Volume? = null,
    val uvIndex: Float? = null
) {

    constructor(jsonObject: JSONObject): this(
        jsonObject.getOrElse(DT, 0L),
        jsonObject.getOrElse(TEMP, 0f),
        jsonObject.getOrElse(FEELS_LIKE, 0f),
        jsonObject.getOrElse(PRESSURE, 0),
        jsonObject.getOrElse(HUMIDITY, 0),
        jsonObject.getOrElse(DEW_POINT, 0f),
        jsonObject.getOrElse(CLOUDS, 0),
        jsonObject.getOrElse(WIND_SPEED, 0f),
        jsonObject.getOrElse(WIND_DEGREES, 0),
        jsonObject.getOrNull<JSONArray>(WEATHER)?.map(::Weather) ?: emptyList(),
        jsonObject.getOrNull(VISIBILITY),
        jsonObject.getOrNull(WIND_GUST),
        jsonObject.getOrNull<JSONObject>(RAIN)?.let(::Volume),
        jsonObject.getOrNull<JSONObject>(SNOW)?.let(::Volume),
        jsonObject.getOrNull(UV_INDEX)
    )

    companion object {

        private const val DT = "dt"
        private const val TEMP = "temp"
        private const val PRESSURE = "pressure"
        private const val HUMIDITY = "humidity"
        private const val CLOUDS = "clouds"
        private const val VISIBILITY = "visibility"
        private const val WEATHER = "weather"
        private const val RAIN = "rain"
        private const val SNOW = "snow"
        private const val UV_INDEX = "uvi"
        private const val FEELS_LIKE = "feels_like"
        private const val DEW_POINT = "dew_point"
        private const val WIND_SPEED = "wind_speed"
        private const val WIND_DEGREES = "wind_deg"
        private const val WIND_GUST = "wind_gust"
    }
}
