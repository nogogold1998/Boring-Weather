package com.sunasterisk.boringweather.data.source.local.room

import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.CurrentWeather
import com.sunasterisk.boringweather.data.model.DailyWeather
import com.sunasterisk.boringweather.data.model.DetailWeather
import com.sunasterisk.boringweather.data.model.HourlyWeather
import com.sunasterisk.boringweather.data.model.SummaryWeather
import com.sunasterisk.boringweather.util.TimeUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OneCallWeatherRepository(
    private val remote: OneCallWeatherDataSource.Remote,
    private val local: OneCallWeatherDataSource.Local,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val timeUtils: TimeUtils = TimeUtils
) : OneCallWeatherDataSource {
    override suspend fun fetchWeatherData(city: City) = withContext(dispatcher) {
        val oneCallEntry = remote.fetchOneCallWeatherByCoordinate(city.coordinate)
        local.insertOneCallWeather(city.id, oneCallEntry)
    }

    @ExperimentalCoroutinesApi
    override fun getCurrentWeather(
        city: City,
        currentDateTime: Long
    ): Flow<CurrentWeather> {
        val (startOfDay, endOfDay) = timeUtils.getStartEndOfDay(currentDateTime)
        return local.getRawCurrentWeather(city, startOfDay, endOfDay)
            .flowOn(Dispatchers.Default)
            .map { (hourlyEntities, dailyEntities) ->
                val hourlyWeathers = hourlyEntities.map(HourlyWeatherEntity::toHourlyWeather)
                val current = hourlyWeathers.lastOrNull { it.dateTime <= currentDateTime }
                val dailyWeathers = dailyEntities.map(DailyWeatherEntity::toDailyWeather)
                val today = dailyWeathers.lastOrNull { it.dateTime <= currentDateTime }
                CurrentWeather(
                    city,
                    current ?: HourlyWeather.default,
                    today ?: DailyWeather.default,
                    hourlyWeathers.map {
                        SummaryWeather(
                            it.dateTime,
                            it.temperature,
                            it.weathers.firstOrNull()?.icon
                        )
                    },
                    dailyWeathers.map {
                        SummaryWeather(
                            it.dateTime,
                            it.temperature.average,
                            it.weathers.firstOrNull()?.icon
                        )
                    }
                )
            }
    }

    @ExperimentalCoroutinesApi
    override fun getDetailWeather(
        city: City,
        dailyWeatherDateTime: Long,
        forceNetwork: Boolean
    ): Flow<DetailWeather> {
        val (startOfDay, endOfDay) = timeUtils.getStartEndOfDay(dailyWeatherDateTime)
        return local.getRawDetailWeather(city, startOfDay, endOfDay)
            .flowOn(Dispatchers.Default)
            .map { (dailyEntity, hourlyEntities) ->
                DetailWeather(
                    city,
                    dailyEntity?.toDailyWeather() ?: DailyWeather.default,
                    hourlyEntities.map(HourlyWeatherEntity::toHourlyWeather)
                )
            }
    }
}
