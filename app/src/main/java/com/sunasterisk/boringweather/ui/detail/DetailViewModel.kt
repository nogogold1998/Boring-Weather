package com.sunasterisk.boringweather.ui.detail

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.Event
import com.sunasterisk.boringweather.base.Single
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.DetailWeather
import com.sunasterisk.boringweather.data.source.local.room.OneCallWeatherDataSource
import com.sunasterisk.boringweather.ui.current.NavigateToDetailsFragmentRequest
import com.sunasterisk.boringweather.ui.detail.model.DailyWeatherItem
import com.sunasterisk.boringweather.ui.detail.model.DetailWeatherAdapterItem
import com.sunasterisk.boringweather.ui.detail.model.HourlyWeatherItem
import com.sunasterisk.boringweather.util.Constants
import com.sunasterisk.boringweather.util.gifUrl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

@FlowPreview
@ExperimentalCoroutinesApi
class DetailViewModel(
    private val oneCallRepo: OneCallWeatherDataSource,
) : ViewModel() {

    private val _requestChannel = ConflatedBroadcastChannel<NavigateToDetailsFragmentRequest>()

    private val _detailWeather =
        _requestChannel.asFlow().transform { (cityId, dailyDt, _) ->
            emitAll(oneCallRepo.getDetailWeather(cityId, dailyDt))
        }

    val city: LiveData<City> = _detailWeather.map { it.city }.asLiveData()

    val detailWeatherAdapterItems: LiveData<List<DetailWeatherAdapterItem<*>>> =
        _detailWeather.map { generateDetailWeatherAdapterItemList(it) }.asLiveData()

    private val focusHourlyWeatherDateTime = Single<Long>()
    private val _focusItemIndexEvent = MutableLiveData<Event<Int>>()
    val focusItemIndexEvent: LiveData<Event<Int>> get() = _focusItemIndexEvent

    val decorImageUrl: LiveData<String?> =
        _detailWeather.map { it.dailyWeather.weathers.firstOrNull()?.gifUrl }.asLiveData()

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    private val _errorRes = MutableLiveData<@StringRes Int>()
    val errorRes: LiveData<Int> get() = _errorRes

    fun loadDetailWeather(request: NavigateToDetailsFragmentRequest) {
        _requestChannel.offer(request)
        focusHourlyWeatherDateTime.value = request.focusHourlyWeatherDateTime
    }

    fun refreshDetailWeather() {
        viewModelScope.launch {
            _isRefreshing.postValue(true)
            try {
                withTimeout(Constants.REQUEST_TIMED_OUT) {
                    city.value?.let { oneCallRepo.fetchWeatherData(it) }
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

    private fun generateDetailWeatherAdapterItemList(
        detail: DetailWeather,
    ): List<DetailWeatherAdapterItem<*>> {
        val focusDt = focusHourlyWeatherDateTime.value
        return listOf(
            DailyWeatherItem(detail.dailyWeather),
            *detail.hourlyWeathers
                .mapIndexed { index, it ->
                    if (it.dateTime == focusDt) _focusItemIndexEvent.postValue(Event(index + 1))
                    HourlyWeatherItem(it, it.dateTime == focusDt)
                }
                .toTypedArray()
        )
    }

    class Factory(private val oneCallRepo: OneCallWeatherDataSource) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DetailViewModel(oneCallRepo) as T
        }
    }
}
