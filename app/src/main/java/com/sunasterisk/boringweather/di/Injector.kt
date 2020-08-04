package com.sunasterisk.boringweather.di

import android.content.Context
import com.sunasterisk.boringweather.data.repository.CityRepository
import com.sunasterisk.boringweather.data.repository.OneCallWeatherRepository
import com.sunasterisk.boringweather.data.source.local.AppDatabase
import com.sunasterisk.boringweather.data.source.local.LocalCityDataSource
import com.sunasterisk.boringweather.data.source.local.LocalOneCallWeatherDataSource
import com.sunasterisk.boringweather.data.source.local.dao.CityDaoImpl
import com.sunasterisk.boringweather.data.source.local.dao.DailyWeatherDaoImpl
import com.sunasterisk.boringweather.data.source.local.dao.HourlyWeatherDaoImpl
import com.sunasterisk.boringweather.data.source.remote.RemoteOneCallWeatherDataSource

object Injector {
    fun getOneCallRepository(context: Context): OneCallWeatherRepository {
        val appDatabase = AppDatabase.getInstance(context)
        val cityDao = CityDaoImpl.getInstance(appDatabase)
        val hourlyWeatherDao = HourlyWeatherDaoImpl.getInstance(appDatabase)
        val dailyWeatherDao = DailyWeatherDaoImpl.getInstance(appDatabase)
        val localOneCallWeatherDataSource =
            LocalOneCallWeatherDataSource.getInstance(cityDao, hourlyWeatherDao, dailyWeatherDao)
        val remoteOneCallWeatherDataSource = RemoteOneCallWeatherDataSource.getInstance()
        return OneCallWeatherRepository.getInstance(
            localOneCallWeatherDataSource,
            remoteOneCallWeatherDataSource
        )
    }

    fun getCityRepository(context: Context): CityRepository {
        val appDatabase = AppDatabase.getInstance(context)
        val cityDao = CityDaoImpl.getInstance(appDatabase)
        val localCityDataSource = LocalCityDataSource.getInstance(cityDao)
        return CityRepository.getInstance(localCityDataSource)
    }
}
