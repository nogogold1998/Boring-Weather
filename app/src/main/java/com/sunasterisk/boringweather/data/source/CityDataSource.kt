package com.sunasterisk.boringweather.data.source

import com.sunasterisk.boringweather.base.Cancellable
import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.Coordinate

interface CityDataSource : Cancellable {
    fun getCityById(cityId: Int, callback: (Result<City>) -> Unit)

    fun findCityByName(cityName: String, limit: Int? = null, callback: (Result<List<City>>) -> Unit)

    fun insertCity(vararg city: City, callback: (Result<Int>) -> Unit)

    fun getFetchedCities(callback: (Result<List<City>>) -> Unit)

    fun getCityByCoordinate(coordinate: Coordinate, callback: (Result<City>) -> Unit)
}
