package com.sunasterisk.boringweather.data.source

import com.sunasterisk.boringweather.base.Cancellable
import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.Coordinate
import com.sunasterisk.boringweather.data.model.CurrentWeather
import com.sunasterisk.boringweather.data.model.DetailWeather
import com.sunasterisk.boringweather.data.model.OneCallEntry

interface OneCallWeatherDataSource : Cancellable {
    fun getCurrentWeather(
        city: City,
        currentDateTime: Long,
        forceNetwork: Boolean,
        callback: (Result<CurrentWeather>) -> Unit
    )

    fun getDetailWeather(
        city: City,
        forceNetwork: Boolean,
        dateTime: Long,
        callback: (Result<DetailWeather>) -> Unit
    )

    interface Local : Cancellable {
        fun insertOneCallWeather(
            cityId: Int? = null,
            oneCallEntry: OneCallEntry,
            callback: (Result<Boolean>) -> Unit
        )

        fun getCurrentWeather(
            city: City,
            currentDateTime: Long,
            startOfDay: Long,
            endOfDay: Long,
            callback: (Result<CurrentWeather>) -> Unit
        )

        fun getDetailWeather(
            city: City,
            dateTime: Long,
            callback: (Result<DetailWeather>) -> Unit
        )
    }

    interface Remote : Cancellable {
        fun fetchOneCallWeatherByCoordinate(
            coordinate: Coordinate,
            callback: (Result<OneCallEntry>) -> Unit
        )
    }
}
