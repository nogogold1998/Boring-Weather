package com.sunasterisk.boringweather.data.source.local.room

import androidx.annotation.VisibleForTesting
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.Coordinate
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCity(vararg city: City)

    suspend fun getCityByCoordinate(coordinate: Coordinate) =
        getCityByLatLon(coordinate.latitude, coordinate.longitude)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    @Query("SELECT * FROM city ORDER BY ((latitude - :lat) * (latitude - :lat) + (longitude - :lon) * (longitude - :lon)) ASC LIMIT 1")
    abstract suspend fun getCityByLatLon(lat: Float, lon: Float): City?

    @Query("SELECT * FROM city where id = :cityId LIMIT 1")
    abstract suspend fun getCityById(cityId: Int): City?

    // // the || is sql string concatenation operator
    @Query("SELECT * FROM city where name LIKE '%' || :cityName || '%' LIMIT :limit")
    abstract fun findCityByName(cityName: String, limit: Int): Flow<List<City>>

    @Query("SELECT * FROM city where lastFetch > 0 ORDER BY lastFetch DESC")
    abstract fun getFetchedCities(): Flow<List<City>>

    @Query("UPDATE city SET lastFetch = :lastFetch WHERE id = :cityId")
    abstract suspend fun updateFetchedCity(cityId: Int, lastFetch: Long)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    @Query("SELECT * FROM city")
    abstract fun getAllCities(): Flow<List<City>>
}
