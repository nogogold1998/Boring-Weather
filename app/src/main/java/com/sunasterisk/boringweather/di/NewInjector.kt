package com.sunasterisk.boringweather.di

import android.content.Context
import com.sunasterisk.boringweather.data.live.LocationLiveData
import com.sunasterisk.boringweather.data.source.local.room.AppRoomDatabase
import com.sunasterisk.boringweather.data.source.local.room.CityDataSource
import com.sunasterisk.boringweather.data.source.local.room.CityRepository
import com.sunasterisk.boringweather.data.source.local.room.LocalCityDataSource
import com.sunasterisk.boringweather.data.source.local.room.LocalOneCalWeatherDataSource
import com.sunasterisk.boringweather.data.source.local.room.OneCallWeatherDataSource
import com.sunasterisk.boringweather.data.source.local.room.OneCallWeatherRepository
import com.sunasterisk.boringweather.data.source.local.room.RemoteOneCallWeatherDataSource
import com.sunasterisk.boringweather.data.source.remote.api.OpenWeatherApiService

object NewInjector {
    private var roomDatabase: AppRoomDatabase? = null
    private var localCityDataSource: CityDataSource.Local? = null
    private var cityRepository: CityDataSource? = null
    private var oneCallWeatherRepository: OneCallWeatherDataSource? = null
    private var localOneCallWeather: OneCallWeatherDataSource.Local? = null
    private var remoteOneCallWeather: OneCallWeatherDataSource.Remote? = null
    private var openWeatherApiService: OpenWeatherApiService? = null

    fun provideCityRepository(context: Context) =
        cityRepository ?: CityRepository(provideLocalCityDataSource(context))
            .also { cityRepository = it }

    private fun provideLocalCityDataSource(context: Context) =
        localCityDataSource ?: LocalCityDataSource(provideAppRoomDb(context).cityDao())
            .also { localCityDataSource = it }

    private lateinit var locationLiveData: LocationLiveData

    fun provideLocationLiveData(context: Context): LocationLiveData {
        if (!this::locationLiveData.isInitialized) locationLiveData = LocationLiveData(context)
        return locationLiveData
    }

    fun provideOneCallWeatherRepository(context: Context): OneCallWeatherDataSource =
        oneCallWeatherRepository ?: OneCallWeatherRepository(
            provideRemoteOneCallWeather(),
            provideLocalOneCallWeather(context),
            provideLocalCityDataSource(context)
        ).also { oneCallWeatherRepository = it }

    private fun provideLocalOneCallWeather(context: Context): OneCallWeatherDataSource.Local {
        val room = provideAppRoomDb(context)
        return localOneCallWeather ?: LocalOneCalWeatherDataSource(
            room.cityDao(),
            room.hourlyWeatherDao(),
            room.dailyWeatherDao()
        ).also { localOneCallWeather = it }
    }

    private fun provideRemoteOneCallWeather() =
        remoteOneCallWeather ?: RemoteOneCallWeatherDataSource(
            provideOpenWeatherApiService()
        ).also { remoteOneCallWeather = it }

    private fun provideOpenWeatherApiService() =
        openWeatherApiService ?: OpenWeatherApiService.create().also { openWeatherApiService = it }

    private fun provideAppRoomDb(context: Context) =
        roomDatabase ?: AppRoomDatabase.getInstance(context).also { roomDatabase = it }
}
