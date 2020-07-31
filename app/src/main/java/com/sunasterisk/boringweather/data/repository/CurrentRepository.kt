package com.sunasterisk.boringweather.data.repository

import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.CurrentWeather

interface CurrentRepository {
    fun getCurrentWeather(
        city: City,
        forceNetwork: Boolean,
        callback: (Result<CurrentWeather>) -> Unit
    )

    fun stopTask()
}
