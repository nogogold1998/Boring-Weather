package com.sunasterisk.boringweather.data.source.local.room

import androidx.annotation.VisibleForTesting
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.util.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(vararg city: City)

    @Query("SELECT * FROM city ORDER BY ((latitude - :lat) * (latitude - :lat) + (longitude - :lon) * (longitude - :lon)) ASC LIMIT 1")
    suspend fun getCityByLatLon(lat: Float, lon: Float): City?

    @Query("SELECT * FROM city where id = :cityId LIMIT 1")
    suspend fun getCityById(cityId: Int): City?

    // // the || is sql string concatenation operator
    @Query("SELECT * FROM city where name LIKE '%' || :cityName || '%' LIMIT :limit")
    suspend fun findCityByName(
        cityName: String,
        limit: Int = Constants.SEARCH_LIMIT_DEFAULT
    ): List<City>

    @Query("SELECT * FROM city where lastFetch > 0 ORDER BY lastFetch DESC")
    fun getFetchedCities(): Flow<List<City>>

    @Query("UPDATE city SET lastFetch = :lastFetch WHERE id = :cityId")
    suspend fun updateFetchedCity(cityId: Int, lastFetch: Long)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    @Query("SELECT * FROM city")
    fun getAllCities(): Flow<List<City>>
}
