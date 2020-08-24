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
import androidx.navigation.NavDirections
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.Event
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.CurrentWeather
import com.sunasterisk.boringweather.data.source.local.room.OneCallWeatherDataSource
import com.sunasterisk.boringweather.util.Constants
import com.sunasterisk.boringweather.util.DefaultSharedPreferences
import com.sunasterisk.boringweather.util.TimeUtils
import com.sunasterisk.boringweather.util.gifUrl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class CurrentViewModel(
    private val oneCallWeatherRepo: OneCallWeatherDataSource,
    private val defaultSharedPreferences: DefaultSharedPreferences,
    private val timeUtils: TimeUtils = TimeUtils
) : ViewModel() {

    private val _cityId = MutableLiveData(defaultSharedPreferences.selectedCityId)
    private val minutelyFlow = flow {
        while (true) {
            emit(timeUtils.getCurrentInSeconds() + Constants.MINUTE_TO_SECONDS)
            delay(Constants.MINUTE_TO_MILLIS)
        }
    }

    private val _currentWeather = _cityId.asFlow()
        .combineTransform(minutelyFlow) { city, currentInSeconds ->
            emitAll(oneCallWeatherRepo.getCurrentWeather(city, currentInSeconds))
        }

    val currentWeather: LiveData<CurrentWeather> = _currentWeather.asLiveData()

    private val _errorRes = MutableLiveData<@StringRes Int>()
    val errorRes: LiveData<Int> = _errorRes

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    val decorImageUrl: LiveData<String?> =
        currentWeather.map { it.currentWeather.weathers.firstOrNull()?.gifUrl }

    private val _navigationEvent = MutableLiveData<Event<NavDirections>>()
    val navigationEvent: LiveData<Event<NavDirections>> get() = _navigationEvent

    fun loadCurrentWeather(cityId: Int) {
        _cityId.value = cityId
    }

    fun refreshCurrentWeather(city: City?) {
        viewModelScope.launch {
            _isRefreshing.postValue(true)
            try {
                withTimeout(Constants.REQUEST_TIMED_OUT) {
                    city?.let { oneCallWeatherRepo.fetchWeatherData(it) }
                        ?: throw IllegalArgumentException("Refresh current weather with a null City reference!")
                }
            } catch (e: Exception) {
                _errorRes.postValue(R.string.error_unknown)
                e.printStackTrace()
            } finally {
                _isRefreshing.postValue(false)
            }
        }
    }

    fun navigateToDetailsFragment(dateTime: Long, isToday: Boolean) {
        currentWeather.value?.let {
            _navigationEvent.postValue(
                Event(
                    CurrentFragmentDirections.actionCurrentToDetail(it.city.id,
                        if (isToday) it.dailyWeather.dateTime else dateTime,
                        if (isToday) dateTime else 0
                    )
                )
            )
        } ?: _errorRes.postValue(R.string.error_refresh_city_null)
    }

    class Factory(
        private val oneCallWeatherRepo: OneCallWeatherDataSource,
        private val defaultSharedPreferences: DefaultSharedPreferences,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CurrentViewModel(oneCallWeatherRepo, defaultSharedPreferences) as T
        }
    }
}
