package com.sunasterisk.boringweather.ui.current

import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.repository.CityRepository
import com.sunasterisk.boringweather.data.source.OneCallWeatherDataSource
import com.sunasterisk.boringweather.util.TimeUtils

class CurrentPresenter(
    override val view: CurrentContract.View,
    private val oneCallWeatherRepository: OneCallWeatherDataSource,
    private val cityRepository: CityRepository
) : CurrentContract.Presenter {

    override fun loadCityById(cityId: Int) = cityRepository.getCityById(cityId) {
        when (it) {
            is Result.Success -> it.data.let { city ->
                view.showCity(city)
                refreshCurrentWeather(city, false)
            }
            is Result.Error -> view.showError(R.string.error_unknown)
        }
    }

    override fun refreshCurrentWeather(city: City, forceNetwork: Boolean) {
        val current = TimeUtils.getCurrentInSeconds()
        oneCallWeatherRepository.getCurrentWeather(city, current, forceNetwork) {
            when (it) {
                is Result.Success -> {
                    val currentWeather = it.data
                    view.showCurrentWeather(currentWeather)
                }
                is Result.Error -> view.showError(R.string.error_refresh_failed)
            }
            view.finishRefresh()
        }
    }

    override fun stopLoadData() {
        oneCallWeatherRepository.cancel()
    }
}
