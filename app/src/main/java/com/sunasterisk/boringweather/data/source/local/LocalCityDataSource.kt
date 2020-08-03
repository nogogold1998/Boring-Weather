package com.sunasterisk.boringweather.data.source.local

import com.sunasterisk.boringweather.base.CallbackAsyncTask
import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.source.CityDataSource
import com.sunasterisk.boringweather.data.source.local.dao.CityDao

class LocalCityDataSource private constructor(private val cityDao: CityDao) : CityDataSource {

    private var callbackAsyncTask: CallbackAsyncTask<*, *>? = null

    override fun getCityById(cityId: Int, callback: (Result<City>) -> Unit) {
        cancel()
        callbackAsyncTask = CallbackAsyncTask<Int, City>(
            handler = {
                cityDao.getCityById(it) ?: throw IllegalArgumentException("wrong city id: $cityId")
            },
            onFinishedListener = {
                it?.let(callback)
            }).apply { executeOnExecutor(cityId) }
    }

    override fun findCityByName(cityName: String, callback: (Result<List<City>>) -> Unit) {
        cancel()
        callbackAsyncTask = CallbackAsyncTask<String, List<City>>(
            handler = {
                cityDao.findCityByName(it)
            },
            onFinishedListener = {
                it?.let(callback)
            }).apply { executeOnExecutor(cityName) }
    }

    override fun insertCity(vararg city: City, callback: (Result<Int>) -> Unit) {
        cancel()
        callbackAsyncTask = CallbackAsyncTask<Array<out City>, Int>(
            handler = {
                cityDao.insertCity(*it)
            },
            onFinishedListener = {
                it?.let(callback)
            }).apply { executeOnExecutor(city) }
    }

    override fun cancel() {
        callbackAsyncTask?.cancel(true)
        callbackAsyncTask = null
    }

    companion object {
        private var instance: LocalCityDataSource? = null

        fun getInstance(cityDao: CityDao) =
            instance ?: LocalCityDataSource(cityDao).also { instance = it }
    }
}
