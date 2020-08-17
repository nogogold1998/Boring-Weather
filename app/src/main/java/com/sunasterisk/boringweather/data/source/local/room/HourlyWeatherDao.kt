package com.sunasterisk.boringweather.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sunasterisk.boringweather.data.model.HourlyWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Dao
abstract class HourlyWeatherDao {

    suspend fun insertHourlyWeather(
        cityId: Int,
        vararg hourlyWeather: HourlyWeather
    ) {
        val hourlyWeatherEntities = withContext(Dispatchers.Default) {
            hourlyWeather.map { HourlyWeatherEntity(cityId, it) }.toTypedArray()
        }
        insertHourlyWeatherEntity(*hourlyWeatherEntities)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertHourlyWeatherEntity(
        vararg hourlyWeatherEntity: HourlyWeatherEntity
    )

    suspend fun getHourWeather(cityId: Int, upperDateTime: Long) =
        getHourlyWeatherEntity(cityId, upperDateTime)?.toHourlyWeather()

    @Query("SELECT * FROM hourly_weather WHERE cityId = :cityId AND dateTime <= :upperDateTime ORDER BY dateTime DESC LIMIT 1")
    protected abstract suspend fun getHourlyWeatherEntity(
        cityId: Int,
        upperDateTime: Long
    ): HourlyWeatherEntity?

    suspend fun findHourlyWeather(
        cityId: Int,
        fromDateTime: Long = 0, toDateTime: Long = Long.MAX_VALUE,
        limit: Int = 500
    ) =
        findHourlyWeatherEntity(cityId, fromDateTime, toDateTime, limit)
            .map { it.map(HourlyWeatherEntity::toHourlyWeather) }

    @Query("SELECT * FROM hourly_weather WHERE cityId = :cityId AND dateTime BETWEEN :fromDateTime AND :toDateTime")
    protected abstract suspend fun findHourlyWeatherEntity(
        cityId: Int,
        fromDateTime: Long = 0, toDateTime: Long = Long.MAX_VALUE,
        limit: Int = 500
    ): Flow<List<HourlyWeatherEntity>>

    @Query("DELETE FROM hourly_weather")
    abstract suspend fun deleteAllHourWeather()
}
