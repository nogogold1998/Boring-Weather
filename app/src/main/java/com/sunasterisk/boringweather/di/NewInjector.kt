package com.sunasterisk.boringweather.di

import android.content.Context
import com.sunasterisk.boringweather.data.live.LocationLiveData
import com.sunasterisk.boringweather.data.source.local.room.AppRoomDatabase
import com.sunasterisk.boringweather.data.source.local.room.CityDataSource
import com.sunasterisk.boringweather.data.source.local.room.CityRepository
import com.sunasterisk.boringweather.data.source.local.room.LocalCityDataSource

object NewInjector {
    private lateinit var roomDatabase: AppRoomDatabase
    private lateinit var localCityDataSource: CityDataSource.Local
    private lateinit var cityRepository: CityDataSource

    fun provideCityRepository(context: Context): CityDataSource {
        if (!this::roomDatabase.isInitialized) roomDatabase = AppRoomDatabase.getInstance(context)
        if (!this::localCityDataSource.isInitialized) {
            localCityDataSource = LocalCityDataSource(roomDatabase.cityDao())
        }
        if (!this::cityRepository.isInitialized) {
            cityRepository = CityRepository(localCityDataSource)
        }
        return cityRepository
    }

    private lateinit var locationLiveData: LocationLiveData

    fun provideLocationLiveData(context: Context): LocationLiveData {
        if (!this::locationLiveData.isInitialized) locationLiveData = LocationLiveData(context)
        return locationLiveData
    }
}
