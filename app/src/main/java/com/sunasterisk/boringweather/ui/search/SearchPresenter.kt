package com.sunasterisk.boringweather.ui.search

import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.data.source.CityDataSource

class SearchPresenter(
    override val view: SearchContract.View,
    private val cityRepository: CityDataSource
) : SearchContract.Presenter {

    override fun searchCity(input: String) {
        cityRepository.findCityByName(input){
            when(it){
                is Result.Success -> view.showSearchResult(it.data)
                is Result.Error -> view.showError(R.string.error_search_result_error)
            }
        }
    }
}
