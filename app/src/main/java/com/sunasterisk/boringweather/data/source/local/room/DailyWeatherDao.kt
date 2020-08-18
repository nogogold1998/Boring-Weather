package com.sunasterisk.boringweather.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DailyWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDailyWeather(vararg dailyWeatherEntity: DailyWeatherEntity)

    @Query("SELECT * FROM daily_weather WHERE cityId = :cityId AND dateTime BETWEEN :fromDateTime AND :toDateTime")
    abstract fun findDailyWeatherEntity(
        cityId: Int,
        fromDateTime: Long, toDateTime: Long
    ): Flow<List<DailyWeatherEntity>>

    @Query("SELECT * FROM daily_weather WHERE cityId = :cityId AND dateTime <= :upperDateTime ORDER BY dateTime LIMIT 1")
    abstract fun getDailyWeatherEntity(
        cityId: Int,
        upperDateTime: Long
    ): Flow<DailyWeatherEntity?>

    @Query("DELETE FROM daily_weather")
    abstract suspend fun deleteAllDailyWeather()
}
