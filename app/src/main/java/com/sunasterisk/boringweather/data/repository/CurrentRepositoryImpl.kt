package com.sunasterisk.boringweather.data.repository

import android.os.AsyncTask
import com.sunasterisk.boringweather.base.CallbackAsyncTask
import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.CurrentWeather

class CurrentRepositoryImpl :
    CurrentRepository {

    private var asyncTask: CallbackAsyncTask<*, *>? = null

    override fun getCurrentWeather(
        city: City,
        forceNetwork: Boolean,
        callback: (Result<CurrentWeather>) -> Unit
    ) {
        asyncTask = CallbackAsyncTask<City, CurrentWeather>(
            handler = {
                Thread.sleep(2000) // TODO implement after implement data sources
                CurrentWeather.default
            },
            onFinishedListener = {
                callback(it ?: Result.Error(NullPointerException()))
            }
        ).apply { executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, city) }
    }

    override fun stopTask() {
        asyncTask?.cancel(true)
    }
}
