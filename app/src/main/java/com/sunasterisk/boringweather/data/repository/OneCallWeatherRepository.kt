package com.sunasterisk.boringweather.data.repository

import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.CurrentWeather
import com.sunasterisk.boringweather.data.model.DetailWeather
import com.sunasterisk.boringweather.data.source.OneCallWeatherDataSource
import com.sunasterisk.boringweather.util.LastFetchOutDateException
import com.sunasterisk.boringweather.util.TimeUtils
import com.sunasterisk.boringweather.util.isOutDated

class OneCallWeatherRepository private constructor(
    private val local: OneCallWeatherDataSource.Local,
    private val remote: OneCallWeatherDataSource.Remote
) : OneCallWeatherDataSource {
    override fun getCurrentWeather(
        city: City,
        currentDateTime: Long,
        forceNetwork: Boolean,
        callback: (Result<CurrentWeather>) -> Unit
    ) {
        val (startOfDay, endOfDay) =
            TimeUtils.getStartEndOfDay(currentDateTime)
        fetchDataFromNetWork(city, forceNetwork, callback) {
            local.getCurrentWeather(city, currentDateTime, startOfDay, endOfDay, callback)
        }
    }

    override fun getDetailWeather(
        city: City,
        dailyWeatherDateTime: Long,
        forceNetwork: Boolean,
        callback: (Result<DetailWeather>) -> Unit
    ) {
        fetchDataFromNetWork(city, forceNetwork, callback) {
            local.getDetailWeather(city, dailyWeatherDateTime, callback)
        }
    }

    private fun <T : Any> fetchDataFromNetWork(
        city: City,
        forceNetwork: Boolean,
        callback: (Result<T>) -> Unit,
        onFetchFromLocal: () -> Unit
    ) {
        if (!forceNetwork) {
            onFetchFromLocal()
            return
        }
        if (city.isOutDated) remote.fetchOneCallWeatherByCoordinate(city.coordinate) {
            when (it) {
                is Result.Success ->
                    local.insertOneCallWeather(city.id, it.data) { insert ->
                        when (insert) {
                            is Result.Success -> onFetchFromLocal()
                            is Result.Error -> callback(Result.Error(insert.exception))
                        }
                    }
                is Result.Error -> callback(Result.Error(it.exception))
            }
        } else callback(Result.Error(LastFetchOutDateException("City data isn't outdated yet!")))
    }

    override fun cancel() {
        remote.cancel()
        local.cancel()
    }

    companion object {
        private var instance: OneCallWeatherRepository? = null

        fun getInstance(
            localOneCallWeatherDataSource: OneCallWeatherDataSource.Local,
            remoteOneCallWeatherDataSource: OneCallWeatherDataSource.Remote
        ) = instance ?: synchronized(this) {
            instance ?: OneCallWeatherRepository(
                localOneCallWeatherDataSource,
                remoteOneCallWeatherDataSource
            ).also { instance = it }
        }
    }
}
