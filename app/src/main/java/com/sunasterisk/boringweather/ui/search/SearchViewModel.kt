package com.sunasterisk.boringweather.ui.search

import android.util.Log
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.Single
import com.sunasterisk.boringweather.data.live.LocationLiveData
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.Coordinate
import com.sunasterisk.boringweather.data.source.local.room.CityDataSource
import com.sunasterisk.boringweather.ui.search.model.CityItem
import com.sunasterisk.boringweather.util.DefaultSharedPreferences
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchViewModel(
    private val cityRepository: CityDataSource,
    private val locationLiveData: LocationLiveData,
    private val defaultSharedPreferences: DefaultSharedPreferences
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        // TODO handle exceptions here
        val msg = "exceptionHandler: context=$context, throwable=$throwable"
        Log.d(TAG, msg)
        println(msg)
        _errorRes.postValue(R.string.error_unknown)
    }

    private val _errorRes = MutableLiveData<@StringRes Int>()
    val errorRes: LiveData<Single<Int>> = _errorRes.map { Single(it) }

    private val coroutineContext = viewModelScope.coroutineContext + exceptionHandler

    private val _location = locationLiveData.map { Single(it) }

    val searchInput = object : MutableLiveData<String>(defaultSharedPreferences.lastSearchInput) {
        override fun setValue(value: String?) {
            super.setValue(value)
            if (value != null) defaultSharedPreferences.lastSearchInput = value
        }

        override fun postValue(value: String?) {
            super.postValue(value)
            if (value != null) defaultSharedPreferences.lastSearchInput = value
        }
    }

    private val _searchResult: LiveData<List<City>> = searchInput.switchMap { input: String? ->
        if (input.isNullOrBlank()) {
            cityRepository.getFetchedCities().asLiveData(coroutineContext)
        } else {
            liveData(coroutineContext) { emit(cityRepository.findCityByName(input)) }
        }
    }

    private val _cities = MediatorLiveData<List<City>>().apply {
        addSource(_location) {
            it.value?.let { location ->
                viewModelScope.launch(coroutineContext) {
                    postValue(cityRepository.getCityByCoordinate(Coordinate(location)))
                }
            }
        }
        addSource(_searchResult) { postValue(it) }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val cities: LiveData<List<City>>
        get() = _cities

    @ExperimentalCoroutinesApi
    val cityItems: LiveData<List<CityItem>> = _cities.asFlow().map { cities ->
        val selectedCityId = defaultSharedPreferences.selectedCityId
        cities.map { CityItem(it, it.id == selectedCityId) }
    }
        // FIXME unable to test
        .flowOn(coroutineContext + Dispatchers.Default)
        .conflate()
        .asLiveData(coroutineContext)

    fun searchCityByLocation() = locationLiveData

    companion object {
        private const val TAG = "SearchViewModel"
    }

    class Factory(
        private val cityRepository: CityDataSource,
        private val locationLiveData: LocationLiveData,
        private val defaultSharedPreferences: DefaultSharedPreferences
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchViewModel(cityRepository, locationLiveData, defaultSharedPreferences) as T
        }
    }
}
