package com.sunasterisk.boringweather.ui.search

import android.location.Location
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.data.model.Coordinate
import com.sunasterisk.boringweather.data.source.CityDataSource

class SearchPresenter(
    override val view: SearchContract.View,
    private val cityRepository: CityDataSource
) : SearchContract.Presenter {

    override fun searchCity(input: String, limit: Int) {
        cityRepository.findCityByName(input, limit) {
            when (it) {
                is Result.Success -> view.showSearchResult(it.data)
                is Result.Error -> view.showError(R.string.error_search_result_error)
            }
        }
    }

    override fun getFetchedCities() {
        cityRepository.getFetchedCities {
            when (it) {
                is Result.Success -> view.showSearchResult(it.data)
                is Result.Error -> view.showError(R.string.error_search_fetched_cities)
            }
        }
    }

    override fun searchCityByLocation(location: Location) =
        cityRepository.getCityByCoordinate(
            Coordinate(location.longitude.toFloat(), location.latitude.toFloat())
        ) {
            when (it) {
                is Result.Success -> view.showSearchResult(listOf(it.data))
                is Result.Error -> view.showError(R.string.error_search_result_error)
            }
        }
}
