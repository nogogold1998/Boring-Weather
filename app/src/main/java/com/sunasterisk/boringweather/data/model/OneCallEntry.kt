package com.sunasterisk.boringweather.data.model

import com.sunasterisk.boringweather.util.getOrElse
import com.sunasterisk.boringweather.util.getOrNull
import com.sunasterisk.boringweather.util.map
import org.json.JSONArray
import org.json.JSONObject

data class OneCallEntry(
    var latitude: Float = 0f,
    var longitude: Float = 0f,
    var timeZone: String = "",
    var timeZoneOffset: Long = 0,
    var current: HourlyWeather = HourlyWeather(),
    var hourly: List<HourlyWeather> = emptyList(),
    var daily: List<DailyWeather> = emptyList()
) {

    constructor(jsonObject: JSONObject): this(
        jsonObject.getOrElse(LAT, 0f),
        jsonObject.getOrElse(LON, 0f),
        jsonObject.getOrElse(TIMEZONE, ""),
        jsonObject.getOrElse(TIMEZONE_OFFSET, 0),
        jsonObject.getOrNull<JSONObject>(CURRENT)?.let(::HourlyWeather) ?: HourlyWeather(),
        jsonObject.getOrNull<JSONArray>(HOURLY)?.map(::HourlyWeather) ?: emptyList(),
        jsonObject.getOrNull<JSONArray>(DAILY)?.map(::DailyWeather) ?: emptyList()
    )

    companion object {

        private const val LAT = "lat"
        private const val LON = "lon"
        private const val TIMEZONE = "timezone"
        private const val TIMEZONE_OFFSET = "timezone_offset"
        private const val CURRENT = "current"
        private const val HOURLY = "hourly"
        private const val DAILY = "daily"
    }
}
