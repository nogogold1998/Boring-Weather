package com.sunasterisk.boringweather.data.source.local.room

import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.Coordinate
import kotlinx.coroutines.flow.Flow

interface CityDataSource {
    fun getCityByIdFlow(cityId: Int): Flow<City?>

    suspend fun getCityById(cityId: Int): City?

    suspend fun findCityByName(cityName: String, limit: Int? = null): List<City>

    suspend fun insertCity(vararg city: City)

    fun getFetchedCities(): Flow<List<City>>

    suspend fun getCityByCoordinate(coordinate: Coordinate): City?

    interface Local : CityDataSource
}
