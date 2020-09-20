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

    // FIXME: wrong result due to calculating distance
    override fun getCityByCoordinate(coordinate: Coordinate): City? {
        val deltaLon = "(${CityTable.COL_COORDINATE_LON} - ?)"
        val deltaLat = "(${CityTable.COL_COORDINATE_LAT} - ?)"
        val cursor = readableDb.rawQuery(
            """
            |SELECT * FROM ${CityTable.TABLE_NAME}
            |ORDER BY ($deltaLon*$deltaLon + $deltaLat*$deltaLat) ASC
            |LIMIT 1;""".trimMargin()
            ,
            floatArrayOf(
                coordinate.longitude, coordinate.longitude,
                coordinate.latitude, coordinate.latitude
            )
                .map(Float::toString)
                .toTypedArray()
        )
        return cursor?.use { if (it.moveToFirst()) City(it) else null }
    }

    override fun findCityByName(cityName: String, limit: Int?): List<City> {
        val list = cityName.split("\\?".toRegex(), 2)
        val (whereClause, args) = when {
            list.size == 2 ->
                "${CityTable.COL_COUNTRY} LIKE ? AND ${CityTable.COL_NAME} LIKE ?" to
                    arrayOf(list[0].trim(), "%${list[1].trim()}%")
            cityName.contains('?') ->
                "${CityTable.COL_COUNTRY} LIKE ?" to arrayOf(list[0].trim())
            else -> "${CityTable.COL_NAME} LIKE ?" to arrayOf("%${cityName.trim()}%")
        }
        val cursor = readableDb.query(
            CityTable.TABLE_NAME,
            null,
            whereClause,
            args,
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

    override fun getFetchedCities(): List<City> {
        val cursor = readableDb.query(
            CityTable.TABLE_NAME,
            null,
            "${CityTable.COL_LAST_FETCH} > ${CityTable.DEFAULT_COL_LAST_FETCH}",
            null,
            null,
            null,
            "${CityTable.COL_LAST_FETCH} DESC"
        )
        return cursor.use { it.map(::City) }
    }

    override fun updateFetchedCity(cityId: Int, lastFetch: Long) = writableDb.execSQL(
        """
        UPDATE ${CityTable.TABLE_NAME}
        SET ${CityTable.COL_LAST_FETCH} = $lastFetch
        WHERE ${CityTable.COL_ID} = $cityId;""".trimIndent()
    )

    companion object {
        private var instance: CityDaoImpl? = null

        fun getInstance(appDatabase: AppDatabase) = instance ?: synchronized(this) {
            instance ?: CityDaoImpl(appDatabase).also { instance = it }
        }
    }
}
