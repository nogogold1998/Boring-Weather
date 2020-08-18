package com.sunasterisk.boringweather.data.source.local.room

import com.sunasterisk.boringweather.data.model.Coordinate
import com.sunasterisk.boringweather.data.source.remote.api.ApiConstants
import com.sunasterisk.boringweather.data.source.remote.api.OpenWeatherApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteOneCallWeatherDataSource(
    private val apiService: OpenWeatherApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : OneCallWeatherDataSource.Remote {
    override suspend fun fetchOneCallWeatherByCoordinate(coordinate: Coordinate) =
        withContext(dispatcher) {
            apiService.fetchOneCallEntry(
                coordinate.latitude,
                coordinate.longitude,
                ApiConstants.PARAM_EXCLUDE_MINUTELY
            )
        }
}
