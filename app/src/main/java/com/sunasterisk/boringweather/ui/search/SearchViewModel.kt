package com.sunasterisk.boringweather.ui.search

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.sunasterisk.boringweather.data.source.local.room.CityDataSource
import com.sunasterisk.boringweather.ui.search.model.CityItem
import com.sunasterisk.boringweather.util.DefaultSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.supervisorScope

@ExperimentalCoroutinesApi
class SearchViewModel(
    private val cityRepository: CityDataSource,
    private val defaultSharedPreferences: DefaultSharedPreferences
) : ViewModel() {

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

    private val _fetchedCities = cityRepository.getFetchedCities()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val searchResult = searchInput.asFlow()
        .combine(_fetchedCities) { input, fetchedCity ->
            if (input.isNullOrBlank()) {
                fetchedCity
            } else {
                supervisorScope { cityRepository.findCityByName(input.trim()) }
            }
        }

    val cityItems: LiveData<List<CityItem>> = searchResult.map { cities ->
        val selectedCityId = defaultSharedPreferences.selectedCityId
        cities.map { CityItem(it, it.id == selectedCityId) }
    }
        .flowOn(Dispatchers.Default)
        .conflate()
        .asLiveData()

    companion object {
        private const val TAG = "SearchViewModel"
    }

    class Factory(
        private val cityRepository: CityDataSource,
        private val defaultSharedPreferences: DefaultSharedPreferences,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchViewModel(cityRepository, defaultSharedPreferences) as T
        }
    }
}
