package com.sunasterisk.boringweather.data.source.local.dao

import android.database.sqlite.SQLiteOpenHelper
import com.sunasterisk.boringweather.base.OnConflictStrategy
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.Coordinate
import com.sunasterisk.boringweather.data.source.local.AppDatabase
import com.sunasterisk.boringweather.data.source.local.CityTable
import com.sunasterisk.boringweather.util.map

class CityDaoImpl private constructor(sqLiteOpenHelper: SQLiteOpenHelper) : CityDao {
    private val writableDb = sqLiteOpenHelper.writableDatabase
    private val readableDb = sqLiteOpenHelper.readableDatabase

    override fun getCityById(cityId: Int): City? {
        val cursor = readableDb.query(
            CityTable.TABLE_NAME,
            null,
            "${CityTable.COL_ID} = ?",
            arrayOf(cityId.toString()),
            null,
            null,
            null,
            "1"
        )
        return cursor?.use { if (it.moveToFirst()) City(it) else null }
    }

    override fun getCityByCoordinate(coordinate: Coordinate): City? {
        val cursor = readableDb.query(
            CityTable.TABLE_NAME,
            null,
            "${CityTable.COL_COORDINATE_LAT} = ? AND ${CityTable.COL_COORDINATE_LON} = ?",
            arrayOf(coordinate.latitude.toString(), coordinate.longitude.toString()),
            null,
            null,
            null,
            "1"
        )
        return cursor?.use { if (it.moveToFirst()) City(it) else null }
    }

    override fun findCityByName(cityName: String, limit: Int?): List<City> {
        val cursor = readableDb.query(
            CityTable.TABLE_NAME,
            null,
            "${CityTable.COL_NAME} LIKE ?",
            arrayOf("%$cityName%"),
            null,
            null,
            null,
            limit?.toString()
        )
        return cursor.use { it.map(::City) }
    }

    override fun insertCity(vararg city: City, @OnConflictStrategy strategy: Int) = city.count {
        writableDb.insertWithOnConflict(
            CityTable.TABLE_NAME,
            null,
            it.getContentValues(),
            strategy
        ) > 0
    }

    companion object {
        private var instance: CityDaoImpl? = null

        fun getInstance(appDatabase: AppDatabase) = instance ?: synchronized(this) {
            instance ?: CityDaoImpl(appDatabase).also { instance = it }
        }
    }
}
