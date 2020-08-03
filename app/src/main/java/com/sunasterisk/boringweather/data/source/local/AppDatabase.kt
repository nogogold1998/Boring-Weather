package com.sunasterisk.boringweather.data.source.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabase private constructor(
    context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(database: SQLiteDatabase) = with(database) {
        execSQL(CityTable.SQL_CREATE_TABLE)
        execSQL(HourlyWeatherTable.SQL_CREATE_TABLE)
        execSQL(DailyWeatherTable.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(
        database: SQLiteDatabase,
        oldVersion: Int, newVersion: Int
    ) = with(database) {
        execSQL(CityTable.SQL_DROP_TABLE)
        execSQL(HourlyWeatherTable.SQL_DROP_TABLE)
        execSQL(DailyWeatherTable.SQL_DROP_TABLE)
        onCreate(database)
    }

    companion object {
        private const val DATABASE_NAME = "db_boring_weather"
        private const val DATABASE_VERSION = 1

        private var instance: AppDatabase? = null
        fun getInstance(context: Context) =
            instance ?: synchronized(this) { AppDatabase(context).also { instance = it } }
    }
}
