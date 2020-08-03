package com.sunasterisk.boringweather.ui.current

import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.data.repository.CityRepository
import com.sunasterisk.boringweather.data.repository.CurrentRepository
import com.sunasterisk.boringweather.data.model.City

class CurrentPresenter(
    override val view: CurrentContract.View,
    private val currentRepository: CurrentRepository,
    private val cityRepository: CityRepository
) : CurrentContract.Presenter {

    override fun loadCityById(cityId: Int) {
        cityRepository.getCityById(cityId) {
            when (it) {
                is Result.Success -> it.data.let(view::showCity)
                is Result.Error -> view.showError(R.string.error_unknown)
            }
        }
    }

    override fun refreshCurrentWeather(city: City, forceNetwork: Boolean) {
        currentRepository.getCurrentWeather(city, forceNetwork) {
            when (it) {
                is Result.Success -> {
                    val currentWeather = it.data
                    view.showCurrentWeather(currentWeather.currentWeather)
                    view.showDailyWeather(currentWeather.dailyWeather)
                    view.showTodaySummaryWeather(currentWeather.todaySummaryWeathers)
                    view.showForecastSummaryWeather(currentWeather.forecastSummaryWeather)
                }
                is Result.Error -> view.showError(R.string.error_refresh_failed)
            }
            view.finishRefresh()
        }
    }

    override fun stopLoadData() {
        currentRepository.stopTask()
    }

    companion object {
        private const val TAG = "CurrentPresenter"
    }
}
