package com.sunasterisk.boringweather.data.source.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sunasterisk.boringweather.data.model.City

@Database(
    entities = [City::class, DailyWeatherEntity::class, HourlyWeatherEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun cityDao(): CityDao

    abstract fun dailyWeatherDao(): DailyWeatherDao

    abstract fun hourlyWeatherDao(): HourlyWeatherDao

    companion object {
        private const val DATABASE_NAME = "boring-room-database"

        @Volatile
        private var instance: AppRoomDatabase? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: buildAppRoomDatabase(context)
        }

        private fun buildAppRoomDatabase(context: Context) =
            Room.databaseBuilder(context, AppRoomDatabase::class.java, DATABASE_NAME)
                // TODO add prepopulate database here
                .build()
    }
}
