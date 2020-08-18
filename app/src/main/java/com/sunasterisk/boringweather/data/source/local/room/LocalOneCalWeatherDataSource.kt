package com.sunasterisk.boringweather.data.source.local.room

import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.OneCallEntry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class LocalOneCalWeatherDataSource(
    private val cityDao: CityDao,
    private val hourlyWeatherDao: HourlyWeatherDao,
    private val dailyWeatherDao: DailyWeatherDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : OneCallWeatherDataSource.Local {

    override suspend fun insertOneCallWeather(cityId: Int?, oneCallEntry: OneCallEntry) =
        coroutineScope {
            val dbCityId = async(dispatcher) {
                cityId
                    ?: cityDao.getCityByLatLon(oneCallEntry.latitude, oneCallEntry.longitude)?.id
                    ?: throw IllegalArgumentException(
                        "Could not find a properly cityId with given " +
                            "cityId: $cityId and oneCallEntry: $oneCallEntry"
                    )
            }
            launch(dispatcher) {
                val hourlyWeatherEntities = withContext(Dispatchers.Default) {
                    arrayOf(oneCallEntry.current, *oneCallEntry.hourly.toTypedArray())
                        .map { HourlyWeatherEntity(dbCityId.await(), it) }.toTypedArray()
                }
                yield()
                hourlyWeatherDao.insertHourlyWeatherEntity(*hourlyWeatherEntities)
            }
            launch(dispatcher) {
                val dailyWeatherEntities = withContext(Dispatchers.Default) {
                    oneCallEntry.daily.map { DailyWeatherEntity(dbCityId.await(), it) }
                        .toTypedArray()
                }
                yield()
                dailyWeatherDao.insertDailyWeather(*dailyWeatherEntities)
            }
            cityDao.updateFetchedCity(dbCityId.await(), oneCallEntry.current.dateTime)
        }

    @ExperimentalCoroutinesApi
    override fun getRawCurrentWeather(
        city: City,
        startOfDay: Long,
        endOfDay: Long
    ): Flow<Pair<List<HourlyWeatherEntity>, List<DailyWeatherEntity>>> {
        val hourlyWeathersFlow =
            hourlyWeatherDao.findHourlyWeatherEntity(city.id, startOfDay, endOfDay)
        val dailyWeathersFlow =
            dailyWeatherDao.findDailyWeatherEntity(city.id, startOfDay, endOfDay)
        return hourlyWeathersFlow.combine(dailyWeathersFlow) { f1, f2 -> f1 to f2 }
    }

    @ExperimentalCoroutinesApi
    override fun getRawDetailWeather(
        city: City,
        startOfDay: Long,
        endOfDay: Long
    ): Flow<Pair<DailyWeatherEntity?, List<HourlyWeatherEntity>>> {
        val dailyWeather =
            dailyWeatherDao.getDailyWeatherEntity(city.id, startOfDay)
        val hourlyWeather =
            hourlyWeatherDao.findHourlyWeatherEntity(city.id, startOfDay, endOfDay)
        return combine(dailyWeather, hourlyWeather) { f1, f2 -> f1 to f2 }
    }
}
