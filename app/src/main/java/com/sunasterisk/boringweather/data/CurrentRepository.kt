package com.sunasterisk.boringweather.data

import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.data.local.model.City
import com.sunasterisk.boringweather.data.local.model.CurrentWeather

interface CurrentRepository {
    fun getCurrentWeather(
        city: City,
        forceNetwork: Boolean,
        callback: (Result<CurrentWeather>) -> Unit
    )

    fun stopTask()
}
