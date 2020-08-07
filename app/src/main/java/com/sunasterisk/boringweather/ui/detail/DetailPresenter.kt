package com.sunasterisk.boringweather.ui.detail

import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.data.source.CityDataSource
import com.sunasterisk.boringweather.data.source.OneCallWeatherDataSource
import com.sunasterisk.boringweather.ui.detail.model.LoadDetailWeatherRequest

class DetailPresenter(
    override val view: DetailContract.View,
    private val oneCallWeatherRepository: OneCallWeatherDataSource,
    private val cityRepository: CityDataSource
) : DetailContract.Presenter {

    override fun loadDetailWeather(request: LoadDetailWeatherRequest) {
        cityRepository.getCityById(request.cityId) { cityResult ->
            when (cityResult) {
                is Result.Success -> cityResult.data.let { city ->
                    oneCallWeatherRepository.getDetailWeather(
                        city,
                        request.dailyWeatherDateTime,
                        request.refresh
                    ) { detailResult ->
                        when (detailResult) {
                            is Result.Success -> view.showDetailWeather(detailResult.data)
                            is Result.Error -> {
                                view.showCity(city)
                                view.showError(R.string.error_refresh_result_null)
                            }
                        }
                    }
                }
                is Result.Error -> view.showError(R.string.error_refresh_city_null)
            }
        }
    }

    override fun cancel() {
        oneCallWeatherRepository.cancel()
    }
}
