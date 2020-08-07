package com.sunasterisk.boringweather.data.source.local

import com.sunasterisk.boringweather.base.CallbackAsyncTask
import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.Coordinate
import com.sunasterisk.boringweather.data.model.CurrentWeather
import com.sunasterisk.boringweather.data.model.DetailWeather
import com.sunasterisk.boringweather.data.model.OneCallEntry
import com.sunasterisk.boringweather.data.model.SummaryWeather
import com.sunasterisk.boringweather.data.source.OneCallWeatherDataSource
import com.sunasterisk.boringweather.data.source.local.dao.CityDao
import com.sunasterisk.boringweather.data.source.local.dao.DailyWeatherDao
import com.sunasterisk.boringweather.data.source.local.dao.HourlyWeatherDao
import com.sunasterisk.boringweather.util.TimeUtils

class LocalOneCallWeatherDataSource(
    private val cityDao: CityDao,
    private val hourlyWeatherDao: HourlyWeatherDao,
    private val dailyWeatherDao: DailyWeatherDao
) : OneCallWeatherDataSource.Local {

    private var callbackAsyncTask: CallbackAsyncTask<*, *>? = null
    override fun insertOneCallWeather(
        cityId: Int?,
        oneCallEntry: OneCallEntry,
        callback: (Result<Boolean>) -> Unit
    ) {
        cancel()
        callbackAsyncTask =
            CallbackAsyncTask<Pair<Int?, OneCallEntry>, Boolean>(
                handler = { (cityId, oneCallEntry) ->
                    var dbCityId: Int? = cityId
                    if (dbCityId == null) {
                        val coordinate = Coordinate(oneCallEntry.longitude, oneCallEntry.latitude)
                        dbCityId = cityDao.getCityByCoordinate(coordinate)?.id ?: 0
                    }
                    val willBeInsertedHourlyWeatherArr =
                        arrayOf(oneCallEntry.current, *oneCallEntry.hourly.toTypedArray())
                    hourlyWeatherDao.insertHourlyWeather(dbCityId, *willBeInsertedHourlyWeatherArr)
                    dailyWeatherDao.insertDailyWeather(dbCityId, *oneCallEntry.daily.toTypedArray())
                    true
                },
                onFinishedListener = {
                    it?.let(callback)
                }).apply { executeOnExecutor(cityId to oneCallEntry) }
    }

    override fun getCurrentWeather(
        city: City,
        currentDateTime: Long,
        startOfDay: Long,
        endOfDay: Long,
        callback: (Result<CurrentWeather>) -> Unit
    ) {
        cancel()
        callbackAsyncTask =
            CallbackAsyncTask<Unit, CurrentWeather>(
                handler = {
                    val currentHourlyWeather =
                        hourlyWeatherDao.getHourlyWeather(city.id, currentDateTime)
                    val hourlyWeathers =
                        hourlyWeatherDao.findHourlyWeather(city.id, startOfDay, endOfDay)
                    val dailyWeathers =
                        dailyWeatherDao.findDailyWeather(city.id, currentDateTime)
                    val todayDailyWeather =
                        dailyWeatherDao.getDailyWeather(city.id, currentDateTime)
                    CurrentWeather(
                        city,
                        currentHourlyWeather ?: CurrentWeather.default.currentWeather,
                        todayDailyWeather ?: CurrentWeather.default.dailyWeather,
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
                },
                onFinishedListener = {
                    it?.let(callback)
                }).apply { executeOnExecutor(Unit) }
    }

    override fun getDetailWeather(
        city: City,
        dateTime: Long,
        callback: (Result<DetailWeather>) -> Unit
    ) {
        val (startOfDay, endOfDay) = TimeUtils.getStartEndOfDay(dateTime)
        cancel()
        callbackAsyncTask =
            CallbackAsyncTask<Unit, DetailWeather>(
                handler = {
                    val dailyWeather = dailyWeatherDao.getDailyWeather(city.id, dateTime)
                    val hourlyWeathers = hourlyWeatherDao.findHourlyWeather(
                        city.id,
                        startOfDay,
                        endOfDay
                    )
                    DetailWeather(
                        city,
                        dailyWeather ?: DetailWeather.default.dailyWeather,
                        hourlyWeathers
                    )
                },
                onFinishedListener = {
                    it?.let(callback)
                }).apply { executeOnExecutor(Unit) }
    }

    override fun cancel() {
        callbackAsyncTask?.cancel(true)
        callbackAsyncTask = null
    }

    companion object {
        private var instance: LocalOneCallWeatherDataSource? = null

        fun getInstance(
            cityDao: CityDao,
            hourlyWeatherDao: HourlyWeatherDao,
            dailyWeatherDao: DailyWeatherDao
        ) = instance ?: synchronized(this) {
            instance ?: LocalOneCallWeatherDataSource(
                cityDao,
                hourlyWeatherDao,
                dailyWeatherDao
            ).also { instance = it }
        }
    }
}
