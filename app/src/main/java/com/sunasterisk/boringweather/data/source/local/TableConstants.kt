package com.sunasterisk.boringweather.data.source.local

object CityTable {
    const val TABLE_NAME = "city"

    const val COL_ID = "id"
    const val COL_NAME = "name"
    const val COL_COUNTRY = "country"
    const val COL_LAST_FETCH = "last_fetch"
    const val DEFAULT_COL_LAST_FETCH = 0L
    const val COL_COORDINATE_LAT = "lat"
    const val COL_COORDINATE_LON = "lon"
}

object HourlyWeatherTable {
    const val TABLE_NAME = "hourly_weather"

    const val COL_DATE_TIME = "date_time"
    const val COL_CITY_ID = "city_id"
    const val COL_TEMPERATURE = "temperature"
    const val COL_FEELS_LIKE = "feels_like"
    const val COL_PRESSURE = "pressure"
    const val COL_HUMIDITY = "humidity"
    const val COL_DEW_POINT = "dew_point"
    const val COL_CLOUDS = "cloud"
    const val COL_WIND_SPEED = "wind_speed"
    const val COL_WIND_DEGREES = "wind_degrees"
    const val COL_WEATHER_ID = "weather_id"
    const val COL_WEATHER_MAIN = "weather_main"
    const val COL_WEATHER_DESCRIPTION = "weather_description"
    const val COL_WEATHER_ICON = "weather_icon"
    const val COL_VISIBILITY = "visibility"
    const val COL_UV_INDEX = "uv_index"
}

object DailyWeatherTable {
    const val TABLE_NAME = "daily_weather"

    const val COL_DATE_TIME = "date_time"
    const val COL_CITY_ID = "city_id"
    const val COL_SUNRISE = "sunrise"
    const val COL_SUNSET = "sunset"
    const val COL_TEMPERATURE_DAY = "temperature_day"
    const val COL_TEMPERATURE_MIN = "temperature_min"
    const val COL_TEMPERATURE_MAX = "temperature_max"
    const val COL_TEMPERATURE_NIGHT = "temperature_night"
    const val COL_TEMPERATURE_EVENING = "temperature_evening"
    const val COL_TEMPERATURE_MORNING = "temperature_morning"
    const val COL_PRESSURE = "pressure"
    const val COL_HUMIDITY = "humidity"
    const val COL_DEW_POINT = "dew_point"
    const val COL_WIND_SPEED = "wind_speed"
    const val COL_WIND_DEGREES = "wind_degrees"
    const val COL_WEATHER_ID = "weather_id"
    const val COL_WEATHER_MAIN = "weather_main"
    const val COL_WEATHER_DESCRIPTION = "weather_description"
    const val COL_WEATHER_ICON = "weather_icon"
    const val COL_CLOUDS = "clouds"
    const val COL_UV_INDEX = "uv_index"
}
