package com.sunasterisk.boringweather.data.source.local.dao

import android.database.sqlite.SQLiteDatabase
import com.sunasterisk.boringweather.base.OnConflictStrategy
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.Coordinate

interface CityDao {
    fun getCityById(cityId: Int): City?

    fun getCityByCoordinate(coordinate: Coordinate): City?

    fun findCityByName(cityName: String, limit: Int?): List<City>

    fun insertCity(
        vararg city: City,
        @OnConflictStrategy strategy: Int = SQLiteDatabase.CONFLICT_REPLACE
    ): Int

    fun getFetchedCities(): List<City>

    fun updateFetchedCity(cityId: Int, lastFetch: Long)
}
