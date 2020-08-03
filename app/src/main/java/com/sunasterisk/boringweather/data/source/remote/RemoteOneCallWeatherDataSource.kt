package com.sunasterisk.boringweather.data.source.remote

import com.sunasterisk.boringweather.base.CallbackAsyncTask
import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.data.model.Coordinate
import com.sunasterisk.boringweather.data.model.OneCallEntry
import com.sunasterisk.boringweather.data.source.OneCallWeatherDataSource
import com.sunasterisk.boringweather.data.source.remote.api.ApiService

class RemoteOneCallWeatherDataSource private constructor() : OneCallWeatherDataSource.Remote {

    private var callbackAsyncTask: CallbackAsyncTask<*, *>? = null

    override fun fetchOneCallWeatherByCoordinate(
        coordinate: Coordinate,
        callback: (Result<OneCallEntry>) -> Unit
    ) {

        callbackAsyncTask = CallbackAsyncTask<Coordinate, OneCallEntry>({
            ApiService.queryOneCallApi(it)
        }, {
            it?.let(callback)
        }).apply { executeOnExecutor(coordinate) }
    }

    override fun cancel() {
        callbackAsyncTask?.cancel(true)
        callbackAsyncTask = null
    }

    companion object {
        private var instance: OneCallWeatherDataSource.Remote? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: RemoteOneCallWeatherDataSource().also { instance = it }
        }
    }
}
