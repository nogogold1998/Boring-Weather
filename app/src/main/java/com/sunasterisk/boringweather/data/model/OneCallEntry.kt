package com.sunasterisk.boringweather.data.model

import com.sunasterisk.boringweather.util.getOrElse
import com.sunasterisk.boringweather.util.getOrNull
import com.sunasterisk.boringweather.util.map
import org.json.JSONArray
import org.json.JSONObject

data class OneCallEntry(
    var latitude: Float,
    var longitude: Float,
    var timeZone: String,
    var timeZoneOffset: Long,
    var current: HourlyWeather,
    var hourly: List<HourlyWeather>,
    var daily: List<DailyWeather>
) {

    constructor(jsonObject: JSONObject) : this(
        jsonObject.getOrElse(LAT, default.latitude),
        jsonObject.getOrElse(LON, default.longitude),
        jsonObject.getOrElse(TIMEZONE, default.timeZone),
        jsonObject.getOrElse(TIMEZONE_OFFSET, default.timeZoneOffset),
        jsonObject.getOrNull<JSONObject>(CURRENT)?.let(::HourlyWeather) ?: default.current,
        jsonObject.getOrNull<JSONArray>(HOURLY)?.map(::HourlyWeather) ?: default.hourly,
        jsonObject.getOrNull<JSONArray>(DAILY)?.map(::DailyWeather) ?: default.daily
    )

    companion object {
        val default = OneCallEntry(0f, 0f, "", 0, HourlyWeather.default, emptyList(), emptyList())

        private const val LAT = "lat"
        private const val LON = "lon"
        private const val TIMEZONE = "timezone"
        private const val TIMEZONE_OFFSET = "timezone_offset"
        private const val CURRENT = "current"
        private const val HOURLY = "hourly"
        private const val DAILY = "daily"
    }
}
