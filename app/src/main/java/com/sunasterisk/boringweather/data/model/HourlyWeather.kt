package com.sunasterisk.boringweather.data.model

import android.content.ContentValues
import android.database.Cursor
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import com.sunasterisk.boringweather.data.source.local.HourlyWeatherTable
import com.sunasterisk.boringweather.util.get
import com.sunasterisk.boringweather.util.getOrElse
import com.sunasterisk.boringweather.util.getOrNull
import com.sunasterisk.boringweather.util.map
import org.json.JSONArray
import org.json.JSONObject

data class HourlyWeather @JvmOverloads constructor(
    @SerializedName(DT) val dateTime: Long,
    @SerializedName(TEMP) val temperature: Float,
    @SerializedName(FEELS_LIKE) val feelsLike: Float,
    @SerializedName(PRESSURE) val pressure: Int,
    @SerializedName(HUMIDITY) val humidity: Int,
    @SerializedName(DEW_POINT) val dewPoint: Float,
    @SerializedName(CLOUDS) val clouds: Int,
    @SerializedName(WIND_SPEED) val windSpeed: Float,
    @SerializedName(WIND_DEGREES) val windDegrees: Int,
    @SerializedName(WEATHER) @Ignore val weathers: List<Weather> = emptyList(),
    @SerializedName(VISIBILITY) val visibility: Int?,
    @SerializedName(WIND_GUST) val windGust: Float?,
    @SerializedName(RAIN) @Ignore val rain: Volume? = null,
    @SerializedName(SNOW) @Ignore val snow: Volume? = null,
    @SerializedName(UV_INDEX) val uvIndex: Float?
) {

    @Deprecated("use retrofit gson library instead")
    constructor(jsonObject: JSONObject) : this(
        jsonObject.getOrElse(DT, default.dateTime),
        jsonObject.getOrElse(TEMP, default.temperature),
        jsonObject.getOrElse(FEELS_LIKE, default.feelsLike),
        jsonObject.getOrElse(PRESSURE, default.pressure),
        jsonObject.getOrElse(HUMIDITY, default.humidity),
        jsonObject.getOrElse(DEW_POINT, default.dewPoint),
        jsonObject.getOrElse(CLOUDS, default.clouds),
        jsonObject.getOrElse(WIND_SPEED, default.windSpeed),
        jsonObject.getOrElse(WIND_DEGREES, default.windDegrees),
        jsonObject.getOrNull<JSONArray>(WEATHER)?.map(::Weather) ?: default.weathers,
        jsonObject.getOrNull(VISIBILITY),
        jsonObject.getOrNull(WIND_GUST),
        jsonObject.getOrNull<JSONObject>(RAIN)?.let(::Volume),
        jsonObject.getOrNull<JSONObject>(SNOW)?.let(::Volume),
        jsonObject.getOrNull(UV_INDEX)
    )

    constructor(cursor: Cursor) : this(
        cursor.getOrElse(HourlyWeatherTable.COL_DATE_TIME, default.dateTime),
        cursor.getOrElse(HourlyWeatherTable.COL_TEMPERATURE, default.temperature),
        cursor.getOrElse(HourlyWeatherTable.COL_FEELS_LIKE, default.feelsLike),
        cursor.getOrElse(HourlyWeatherTable.COL_PRESSURE, default.pressure),
        cursor.getOrElse(HourlyWeatherTable.COL_HUMIDITY, default.humidity),
        cursor.getOrElse(HourlyWeatherTable.COL_DEW_POINT, default.dewPoint),
        cursor.getOrElse(HourlyWeatherTable.COL_CLOUDS, default.clouds),
        cursor.getOrElse(HourlyWeatherTable.COL_WIND_SPEED, default.windSpeed),
        cursor.getOrElse(HourlyWeatherTable.COL_WIND_DEGREES, default.windDegrees),
        listOf(
            Weather(
                cursor.getOrElse(HourlyWeatherTable.COL_WEATHER_ID, Weather.default.id),
                cursor.getOrElse(HourlyWeatherTable.COL_WEATHER_MAIN, Weather.default.main),
                cursor.getOrElse(
                    HourlyWeatherTable.COL_WEATHER_DESCRIPTION,
                    Weather.default.description
                ),
                cursor.get(HourlyWeatherTable.COL_WEATHER_ICON)
            )
        ),
        cursor.get(HourlyWeatherTable.COL_VISIBILITY),
        null,
        null,
        null,
        cursor.get(HourlyWeatherTable.COL_UV_INDEX)
    )

    fun getContentValues(cityId: Int) = ContentValues().apply {
        val weather = weathers.firstOrNull() ?: Weather.default
        put(HourlyWeatherTable.COL_DATE_TIME, dateTime)
        put(HourlyWeatherTable.COL_CITY_ID, cityId)
        put(HourlyWeatherTable.COL_TEMPERATURE, temperature)
        put(HourlyWeatherTable.COL_FEELS_LIKE, feelsLike)
        put(HourlyWeatherTable.COL_PRESSURE, pressure)
        put(HourlyWeatherTable.COL_HUMIDITY, humidity)
        put(HourlyWeatherTable.COL_DEW_POINT, dewPoint)
        put(HourlyWeatherTable.COL_CLOUDS, clouds)
        put(HourlyWeatherTable.COL_WIND_SPEED, windSpeed)
        put(HourlyWeatherTable.COL_WIND_DEGREES, windDegrees)
        put(HourlyWeatherTable.COL_WEATHER_ID, weather.id)
        put(HourlyWeatherTable.COL_WEATHER_MAIN, weather.main)
        put(HourlyWeatherTable.COL_WEATHER_DESCRIPTION, weather.description)
        put(HourlyWeatherTable.COL_WEATHER_ICON, weather.icon)
        put(HourlyWeatherTable.COL_VISIBILITY, visibility ?: 0)
        put(HourlyWeatherTable.COL_UV_INDEX, uvIndex ?: 0f)
    }

    companion object {
        val default =
            HourlyWeather(0, 0f, 0f, 0, 0, 0f, 0, 0f, 0, emptyList(), null, null, null, null, null)

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
