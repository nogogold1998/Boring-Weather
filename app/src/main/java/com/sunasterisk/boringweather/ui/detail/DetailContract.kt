package com.sunasterisk.boringweather.ui.detail

import androidx.annotation.StringRes
import com.sunasterisk.boringweather.base.Cancellable
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.DetailWeather
import com.sunasterisk.boringweather.ui.detail.model.LoadDetailWeatherRequest

interface DetailContract {
    interface View {
        val presenter: Presenter?

        fun showError(@StringRes errorStringRes: Int)
        fun showDetailWeather(detailWeather: DetailWeather)
        fun finishRefresh()
        fun showCity(city: City)
    }

    interface Presenter : Cancellable {
        val view: View

        fun loadDetailWeather(request: LoadDetailWeatherRequest)
    }
}
