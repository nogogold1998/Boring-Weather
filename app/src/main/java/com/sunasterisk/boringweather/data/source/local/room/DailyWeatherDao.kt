package com.sunasterisk.boringweather.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sunasterisk.boringweather.data.model.DailyWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Dao
abstract class DailyWeatherDao {

    suspend fun insertDailyWeather(cityId: Int, vararg dailyWeather: DailyWeather) {
        val dailyWeatherEntities = withContext(Dispatchers.Default) {
            dailyWeather.map { DailyWeatherEntity(cityId, it) }.toTypedArray()
        }
        insertDailyWeather(*dailyWeatherEntities)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertDailyWeather(vararg dailyWeatherEntity: DailyWeatherEntity)

    fun findDailyWeather(cityId: Int, fromDateTime: Long = 0, toDateTime: Long = Long.MAX_VALUE) =
        findDailyWeatherEntity(cityId, fromDateTime, toDateTime)
            .map { it.map(DailyWeatherEntity::toDailyWeather) }

    @Query("SELECT * FROM daily_weather WHERE cityId = :cityId AND dateTime BETWEEN :fromDateTime AND :toDateTime")
    protected abstract fun findDailyWeatherEntity(
        cityId: Int,
        fromDateTime: Long, toDateTime: Long
    ): Flow<List<DailyWeatherEntity>>

    suspend fun getDailyWeather(cityId: Int, upperDateTime: Long) =
        getDailyWeatherEntity(cityId, upperDateTime)?.toDailyWeather()

    @Query("SELECT * FROM daily_weather WHERE cityId = :cityId AND dateTime <= :upperDateTime ORDER BY dateTime LIMIT 1")
    protected abstract suspend fun getDailyWeatherEntity(
        cityId: Int,
        upperDateTime: Long
    ): DailyWeatherEntity?

    @Query("DELETE FROM daily_weather")
    abstract suspend fun deleteAllDailyWeather()
}
