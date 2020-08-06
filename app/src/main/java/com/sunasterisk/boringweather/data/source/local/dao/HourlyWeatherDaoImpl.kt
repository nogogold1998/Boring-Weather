package com.sunasterisk.boringweather.data.source.local.dao

import android.database.sqlite.SQLiteOpenHelper
import com.sunasterisk.boringweather.base.OnConflictStrategy
import com.sunasterisk.boringweather.data.model.HourlyWeather
import com.sunasterisk.boringweather.data.source.local.AppDatabase
import com.sunasterisk.boringweather.data.source.local.DailyWeatherTable
import com.sunasterisk.boringweather.data.source.local.HourlyWeatherTable
import com.sunasterisk.boringweather.util.Constants
import com.sunasterisk.boringweather.util.map

class HourlyWeatherDaoImpl private constructor(
    sqLiteOpenHelper: SQLiteOpenHelper
) : HourlyWeatherDao {
    private val writableDb = sqLiteOpenHelper.writableDatabase
    private val readableDb = sqLiteOpenHelper.readableDatabase

    override fun insertHourlyWeather(
        cityId: Int,
        vararg hourlyWeather: HourlyWeather,
        @OnConflictStrategy strategy: Int
    ) = hourlyWeather.count {
        writableDb.insertWithOnConflict(
            HourlyWeatherTable.TABLE_NAME,
            null,
            it.getContentValues(cityId),
            strategy
        ) > 0
    }

    override fun getHourlyWeather(
        cityId: Int,
        upperDateTime: Long
    ): HourlyWeather? {
        val cursor = readableDb.query(
            HourlyWeatherTable.TABLE_NAME,
            null,
            "${HourlyWeatherTable.COL_CITY_ID} = ? AND ${HourlyWeatherTable.COL_DATE_TIME} " +
                "<= (? + ${Constants.MINUTE_TO_SECONDS})",
            arrayOf(cityId.toString(), upperDateTime.toString()),
            null,
            null,
            "${DailyWeatherTable.COL_DATE_TIME} DESC",
            "1"
        )
        return cursor.use { if (it.moveToFirst()) HourlyWeather(it) else null }
    }

    override fun findHourlyWeather(
        cityId: Int,
        fromDateTime: Long?,
        toDateTime: Long?,
        limit: Int?
    ): List<HourlyWeather> {
        val whereClause = StringBuffer("${HourlyWeatherTable.COL_CITY_ID} = ? ")
        if (fromDateTime != null) {
            val colDateTime = HourlyWeatherTable.COL_DATE_TIME
            whereClause.append("AND $colDateTime >= ? ")
            if (toDateTime != null) {
                whereClause.append("AND $colDateTime < ? ")
            }
        }

        val whereArgs = run {
            val cityIdStr = cityId.toString()
            val fromDtStr = fromDateTime?.toString()
            val toDtStr = toDateTime?.toString()
            when {
                fromDtStr != null && toDtStr != null -> arrayOf(cityIdStr, fromDtStr, toDtStr)
                fromDtStr != null -> arrayOf(cityIdStr, fromDtStr)
                else -> arrayOf(cityIdStr)
            }
        }

        val cursor = readableDb.query(
            HourlyWeatherTable.TABLE_NAME,
            null,
            whereClause.toString(),
            whereArgs,
            null,
            null,
            "${HourlyWeatherTable.COL_DATE_TIME} ASC",
            limit?.toString()
        )
        return cursor.use { it.map(::HourlyWeather) }
    }

    override fun deleteAllHourlyWeather() =
        writableDb.delete(HourlyWeatherTable.TABLE_NAME, null, null)

    companion object {
        private var instance: HourlyWeatherDaoImpl? = null

        fun getInstance(appDatabase: AppDatabase) = instance ?: synchronized(this) {
            instance ?: HourlyWeatherDaoImpl(appDatabase).also {
                instance = it
            }
        }
    }
}
