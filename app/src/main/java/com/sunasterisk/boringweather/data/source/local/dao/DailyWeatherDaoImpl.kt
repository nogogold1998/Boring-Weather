package com.sunasterisk.boringweather.data.source.local.dao

import android.database.sqlite.SQLiteOpenHelper
import com.sunasterisk.boringweather.data.model.DailyWeather
import com.sunasterisk.boringweather.data.source.local.AppDatabase
import com.sunasterisk.boringweather.data.source.local.DailyWeatherTable
import com.sunasterisk.boringweather.util.map

class DailyWeatherDaoImpl private constructor(
    sqLiteOpenHelper: SQLiteOpenHelper
) : DailyWeatherDao {
    private val writableDb = sqLiteOpenHelper.writableDatabase
    private val readableDb = sqLiteOpenHelper.readableDatabase

    override fun insertDailyWeather(
        cityId: Int,
        vararg dailyWeather: DailyWeather,
        strategy: Int
    ) = dailyWeather.count {
        writableDb.insertWithOnConflict(
            DailyWeatherTable.TABLE_NAME,
            null,
            it.getContentValues(cityId),
            strategy
        ) > 0
    }

    @ExperimentalStdlibApi
    override fun findDailyWeather(
        cityId: Int,
        fromDateTime: Long?,
        toDateTime: Long?
    ): List<DailyWeather> {
        val whereClause = StringBuffer()
        val whereArgs = buildList {
            whereClause.append("${DailyWeatherTable.COL_CITY_ID} = ? ")
            add(cityId.toString())
            fromDateTime?.let {
                whereClause.append("AND ${DailyWeatherTable.COL_DATE_TIME} >= ? ")
                add(it.toString())
            }
            toDateTime?.let {
                whereClause.append("AND ${DailyWeatherTable.COL_DATE_TIME} <= ?")
                add(it.toString())
            }
        }

        val cursor = readableDb.query(
            DailyWeatherTable.TABLE_NAME,
            null,
            whereClause.toString(),
            whereArgs.toTypedArray(),
            null,
            null,
            "${DailyWeatherTable.COL_DATE_TIME} ASC"
        )
        return cursor.use { it.map(::DailyWeather) }
    }

    override fun getDailyWeather(cityId: Int, upperDateTime: Long): DailyWeather? {
        val cursor = readableDb.query(
            DailyWeatherTable.TABLE_NAME,
            null,
            "${DailyWeatherTable.COL_CITY_ID} = ? AND ${DailyWeatherTable.COL_DATE_TIME} <= ?",
            arrayOf(cityId.toString(), upperDateTime.toString()),
            null,
            null,
            "${DailyWeatherTable.COL_DATE_TIME} DESC",
            "1"
        )
        return cursor.use { if (it.moveToFirst()) DailyWeather(it) else null }
    }

    override fun deleteAllDailyWeather() =
        writableDb.delete(DailyWeatherTable.TABLE_NAME, null, null)

    companion object {
        private var instance: DailyWeatherDaoImpl? = null

        fun getInstance(appDatabase: AppDatabase) = instance ?: synchronized(this) {
            instance ?: DailyWeatherDaoImpl(appDatabase).also { instance = it }
        }
    }
}
