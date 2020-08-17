package com.sunasterisk.boringweather.data.model

import android.content.ContentValues
import android.database.Cursor
import com.google.gson.annotations.SerializedName
import com.sunasterisk.boringweather.data.source.local.DailyWeatherTable
import com.sunasterisk.boringweather.util.get
import com.sunasterisk.boringweather.util.getOrElse
import com.sunasterisk.boringweather.util.getOrNull
import com.sunasterisk.boringweather.util.map
import org.json.JSONArray
import org.json.JSONObject

data class DailyWeather(
    @SerializedName(DT) val dateTime: Long,
    @SerializedName(SUNRISE) val sunrise: Long,
    @SerializedName(SUNSET) val sunset: Long,
    @SerializedName(TEMP) val temperature: Temperature,
    @SerializedName(PRESSURE) val pressure: Int,
    @SerializedName(HUMIDITY) val humidity: Int,
    @SerializedName(DEW_POINT) val dewPoint: Float,
    @SerializedName(WIND_SPEED) val windSpeed: Float,
    @SerializedName(WIND_DEGREES) val windDegrees: Int,
    @SerializedName(WEATHER) val weathers: List<Weather>,
    @SerializedName(CLOUDS) val clouds: Int,
    @SerializedName(UVI) val uvIndex: Float
) {

    @Deprecated("use gson library instead")
    constructor(jsonObject: JSONObject) : this(
        jsonObject.getOrElse(DT, default.dateTime),
        jsonObject.getOrElse(SUNRISE, default.sunrise),
        jsonObject.getOrElse(SUNSET, default.sunset),
        jsonObject.getOrNull<JSONObject>(TEMP)?.let(::Temperature) ?: default.temperature,
        jsonObject.getOrElse(PRESSURE, default.pressure),
        jsonObject.getOrElse(HUMIDITY, default.humidity),
        jsonObject.getOrElse(DEW_POINT, default.dewPoint),
        jsonObject.getOrElse(WIND_SPEED, default.windSpeed),
        jsonObject.getOrElse(WIND_DEGREES, default.windDegrees),
        jsonObject.getOrNull<JSONArray>(WEATHER)?.map(::Weather) ?: default.weathers,
        jsonObject.getOrElse(CLOUDS, default.clouds),
        jsonObject.getOrElse(UVI, default.uvIndex)
    )

    constructor(cursor: Cursor) : this(
        cursor.get(DailyWeatherTable.COL_DATE_TIME) ?: default.dateTime,
        cursor.get(DailyWeatherTable.COL_SUNRISE) ?: default.sunrise,
        cursor.get(DailyWeatherTable.COL_SUNSET) ?: default.sunset,
        Temperature(
            cursor.get(DailyWeatherTable.COL_TEMPERATURE_DAY) ?: Temperature.default.day,
            cursor.get(DailyWeatherTable.COL_TEMPERATURE_MIN) ?: Temperature.default.min,
            cursor.get(DailyWeatherTable.COL_TEMPERATURE_MAX) ?: Temperature.default.max,
            cursor.get(DailyWeatherTable.COL_TEMPERATURE_NIGHT) ?: Temperature.default.night,
            cursor.get(DailyWeatherTable.COL_TEMPERATURE_EVENING) ?: Temperature.default.evening,
            cursor.get(DailyWeatherTable.COL_TEMPERATURE_MORNING) ?: Temperature.default.morning
        ),
        cursor.get(DailyWeatherTable.COL_PRESSURE) ?: default.pressure,
        cursor.get(DailyWeatherTable.COL_HUMIDITY) ?: default.humidity,
        cursor.get(DailyWeatherTable.COL_DEW_POINT) ?: default.dewPoint,
        cursor.get(DailyWeatherTable.COL_WIND_SPEED) ?: default.windSpeed,
        cursor.get(DailyWeatherTable.COL_WIND_DEGREES) ?: default.windDegrees,
        listOf(
            Weather(
                cursor.get(DailyWeatherTable.COL_WEATHER_ID) ?: Weather.default.id,
                cursor.get(DailyWeatherTable.COL_WEATHER_MAIN) ?: Weather.default.main,
                cursor.get(DailyWeatherTable.COL_WEATHER_DESCRIPTION)
                    ?: Weather.default.description,
                cursor.get(DailyWeatherTable.COL_WEATHER_ICON)
            )
        ),
        cursor.get(DailyWeatherTable.COL_CLOUDS) ?: default.clouds,
        cursor.get(DailyWeatherTable.COL_UV_INDEX) ?: default.uvIndex
    )

    fun getContentValues(cityId: Int) = ContentValues().apply {
        val weather = weathers.firstOrNull() ?: Weather.default
        put(DailyWeatherTable.COL_DATE_TIME, dateTime)
        put(DailyWeatherTable.COL_CITY_ID, cityId)
        put(DailyWeatherTable.COL_SUNRISE, sunrise)
        put(DailyWeatherTable.COL_SUNSET, sunset)
        put(DailyWeatherTable.COL_TEMPERATURE_DAY, temperature.day)
        put(DailyWeatherTable.COL_TEMPERATURE_EVENING, temperature.evening)
        put(DailyWeatherTable.COL_TEMPERATURE_MAX, temperature.max)
        put(DailyWeatherTable.COL_TEMPERATURE_MIN, temperature.min)
        put(DailyWeatherTable.COL_TEMPERATURE_MORNING, temperature.morning)
        put(DailyWeatherTable.COL_TEMPERATURE_NIGHT, temperature.night)
        put(DailyWeatherTable.COL_PRESSURE, pressure)
        put(DailyWeatherTable.COL_HUMIDITY, humidity)
        put(DailyWeatherTable.COL_DEW_POINT, dewPoint)
        put(DailyWeatherTable.COL_WIND_SPEED, windSpeed)
        put(DailyWeatherTable.COL_WIND_DEGREES, windDegrees)
        put(DailyWeatherTable.COL_WEATHER_ID, weather.id)
        put(DailyWeatherTable.COL_WEATHER_MAIN, weather.main)
        put(DailyWeatherTable.COL_WEATHER_DESCRIPTION, weather.description)
        put(DailyWeatherTable.COL_WEATHER_ICON, weather.icon)
        put(DailyWeatherTable.COL_CLOUDS, clouds)
        put(DailyWeatherTable.COL_UV_INDEX, uvIndex)
    }

    companion object {
        val default =
            DailyWeather(0, 0, 0, Temperature.default, 0, 0, 0f, 0f, 0, emptyList(), 0, 0f)

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
