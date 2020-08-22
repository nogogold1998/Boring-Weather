package com.sunasterisk.boringweather.ui.current

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.data.model.CurrentWeather
import com.sunasterisk.boringweather.data.source.local.room.OneCallWeatherDataSource
import com.sunasterisk.boringweather.util.DefaultSharedPreferences
import com.sunasterisk.boringweather.util.TimeUtils
import com.sunasterisk.boringweather.util.gifUrl
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

class CurrentViewModel(
    private val oneCallWeatherRepo: OneCallWeatherDataSource,
    private val defaultSharedPreferences: DefaultSharedPreferences,
    private val timeUtils: TimeUtils = TimeUtils
) : ViewModel() {

    private val _cityId = MutableLiveData<Int>(defaultSharedPreferences.selectedCityId)

    private val _currentWeather = _cityId.asFlow().transform {
        emitAll(oneCallWeatherRepo.getCurrentWeather(it, timeUtils.getCurrentInSeconds()))
    }.onEach { _isRefreshing.postValue(false) }

    val currentWeather: LiveData<CurrentWeather> = _currentWeather.asLiveData()

    private val _errorRes = MutableLiveData<@StringRes Int>()
    val errorRes: LiveData<Int> = _errorRes

    private val _isRefreshing = MutableLiveData(true)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    val decorImageUrl: LiveData<String?> =
        currentWeather.map { it.currentWeather.weathers.firstOrNull()?.gifUrl }

    fun loadCurrentWeather(cityId: Int) {
        _cityId.value = cityId
    }

    fun refreshCurrentWeather() = viewModelScope.launch {
        _isRefreshing.postValue(true)
        currentWeather.value?.city?.let { oneCallWeatherRepo.fetchWeatherData(it) }
            ?: _errorRes.postValue(R.string.error_unknown)
    }

    class Factory(
        private val oneCallWeatherRepo: OneCallWeatherDataSource,
        private val defaultSharedPreferences: DefaultSharedPreferences
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CurrentViewModel(oneCallWeatherRepo, defaultSharedPreferences) as T
        }
    }
}
