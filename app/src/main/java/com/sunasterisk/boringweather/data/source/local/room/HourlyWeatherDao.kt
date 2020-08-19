package com.sunasterisk.boringweather.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sunasterisk.boringweather.util.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface HourlyWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyWeatherEntity(vararg hourlyWeatherEntity: HourlyWeatherEntity)

    @Query("SELECT * FROM hourly_weather WHERE cityId = :cityId AND dateTime <= :upperDateTime ORDER BY dateTime DESC LIMIT 1")
    suspend fun getHourlyWeatherEntity(
        cityId: Int,
        upperDateTime: Long
    ): HourlyWeatherEntity?

    @Query("SELECT * FROM hourly_weather WHERE cityId = :cityId AND :fromDateTime <= dateTime AND dateTime < :toDateTime LIMIT :limit")
    fun findHourlyWeatherEntity(
        cityId: Int,
        fromDateTime: Long = 0, toDateTime: Long = Long.MAX_VALUE,
        limit: Int = Constants.SEARCH_LIMIT_DEFAULT
    ): Flow<List<HourlyWeatherEntity>>

    @Query("DELETE FROM hourly_weather")
    suspend fun deleteAllHourWeather()
}
