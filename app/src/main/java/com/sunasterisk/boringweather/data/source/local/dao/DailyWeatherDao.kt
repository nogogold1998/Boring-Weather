package com.sunasterisk.boringweather.data.source.local.dao

import android.database.sqlite.SQLiteDatabase
import com.sunasterisk.boringweather.base.OnConflictStrategy
import com.sunasterisk.boringweather.data.model.DailyWeather

interface DailyWeatherDao {
    fun insertDailyWeather(
        cityId: Int,
        vararg dailyWeather: DailyWeather,
        @OnConflictStrategy strategy: Int = SQLiteDatabase.CONFLICT_REPLACE
    ): Int

    fun findDailyWeather(
        cityId: Int,
        fromDateTime: Long? = null,
        toDateTime: Long? = null
    ): List<DailyWeather>

    fun getDailyWeather(
        cityId: Int,
        upperDateTime: Long
    ): DailyWeather?

    fun deleteAllDailyWeather(): Int
}
