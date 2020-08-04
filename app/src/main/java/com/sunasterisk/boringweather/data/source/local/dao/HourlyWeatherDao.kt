package com.sunasterisk.boringweather.data.source.local.dao

import android.database.sqlite.SQLiteDatabase
import com.sunasterisk.boringweather.base.OnConflictStrategy
import com.sunasterisk.boringweather.data.model.HourlyWeather

interface HourlyWeatherDao {

    fun insertHourlyWeather(
        cityId: Int,
        vararg hourlyWeather: HourlyWeather,
        @OnConflictStrategy strategy: Int = SQLiteDatabase.CONFLICT_REPLACE
    ): Int

    fun getHourlyWeather(cityId: Int, upperDateTime: Long): HourlyWeather?

    fun findHourlyWeather(
        cityId: Int,
        fromDateTime: Long? = null,
        toDateTime: Long? = null
    ): List<HourlyWeather>

    fun deleteAllHourlyWeather(): Int
}
