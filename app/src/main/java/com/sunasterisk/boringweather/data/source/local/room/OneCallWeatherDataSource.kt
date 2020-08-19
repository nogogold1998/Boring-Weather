package com.sunasterisk.boringweather.data.source.local.room

import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.Coordinate
import com.sunasterisk.boringweather.data.model.CurrentWeather
import com.sunasterisk.boringweather.data.model.DetailWeather
import com.sunasterisk.boringweather.data.model.OneCallEntry
import kotlinx.coroutines.flow.Flow

interface OneCallWeatherDataSource {
    suspend fun fetchWeatherData(city: City)

    fun getCurrentWeather(
        cityId: Int,
        currentDateTime: Long
    ): Flow<CurrentWeather>

    fun getDetailWeather(
        cityId: Int,
        dailyWeatherDateTime: Long
    ): Flow<DetailWeather>

    interface Local {

        suspend fun insertOneCallWeather(
            cityId: Int? = null,
            oneCallEntry: OneCallEntry
        )

        fun getRawCurrentWeather(
            cityId: Int,
            startOfDay: Long,
            endOfDay: Long
        ): Flow<Pair<List<HourlyWeatherEntity>, List<DailyWeatherEntity>>>

        fun getRawDetailWeather(
            cityId: Int,
            dailyWeatherDateTime: Long,
            startOfDay: Long,
            endOfDay: Long
        ): Flow<Pair<DailyWeatherEntity?, List<HourlyWeatherEntity>>>
    }

    interface Remote {
        suspend fun fetchOneCallWeatherByCoordinate(coordinate: Coordinate): OneCallEntry
    }
}
