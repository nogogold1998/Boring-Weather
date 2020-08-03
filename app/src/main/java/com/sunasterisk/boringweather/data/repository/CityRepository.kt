package com.sunasterisk.boringweather.data.repository

import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.source.CityDataSource

class CityRepository private constructor(
    private val localCityDataSource: CityDataSource
) : CityDataSource {
    override fun getCityById(cityId: Int, callback: (Result<City>) -> Unit) =
        localCityDataSource.getCityById(cityId, callback)

    override fun findCityByName(cityName: String, callback: (Result<List<City>>) -> Unit) =
        localCityDataSource.findCityByName(cityName, callback)

    override fun insertCity(vararg city: City, callback: (Result<Int>) -> Unit) =
        localCityDataSource.insertCity(*city, callback = callback)

    override fun cancel() = localCityDataSource.cancel()

    companion object {
        private var instance: CityRepository? = null

        fun getInstance(localCityDataSource: CityDataSource) =
            instance ?: synchronized(this) {
                instance ?: CityRepository(localCityDataSource).also {
                    instance = it
                }
            }
    }
}
