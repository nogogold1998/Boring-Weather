package com.sunasterisk.boringweather.data.source.local.room

import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.Coordinate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocalCityDataSource(
    private val cityDao: CityDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CityDataSource {

    override suspend fun getCityById(cityId: Int): City? =
        withContext(dispatcher) { cityDao.getCityById(cityId) }

    override suspend fun findCityByName(cityName: String, limit: Int?): List<City> {
        return withContext(dispatcher) {
            if (limit != null) {
                cityDao.findCityByName(cityName, limit)
            } else {
                cityDao.findCityByName(cityName)
            }
        }
    }

    override suspend fun insertCity(vararg city: City) =
        withContext(dispatcher) { cityDao.insertCity(*city) }

    override fun getFetchedCities(): Flow<List<City>> = cityDao.getFetchedCities()

    override suspend fun getCityByCoordinate(coordinate: Coordinate): City? =
        withContext(dispatcher) {
            cityDao.getCityByLatLon(coordinate.latitude, coordinate.longitude)
        }
}
