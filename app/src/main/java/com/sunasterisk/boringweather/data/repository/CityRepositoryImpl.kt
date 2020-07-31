package com.sunasterisk.boringweather.data.repository

import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.repository.CityRepository

class CityRepositoryImpl :
    CityRepository {
    override fun getCityById(cityId: Int, callback: (Result<City>) -> Unit) {
        callback(Result.Success(City()))
    }

    override fun findCityByName(cityName: String, callback: (Result<List<City>>) -> Unit) {
        callback(Result.Success(emptyList()))
    }

    override fun findCityById(cityId: Int, callback: (Result<List<City>>) -> Unit) {
        callback(Result.Success(emptyList()))
    }

    override fun insertCity(vararg city: City, callback: (Result<Int>) -> Unit) {
        callback(Result.Success(0))
    }
}
