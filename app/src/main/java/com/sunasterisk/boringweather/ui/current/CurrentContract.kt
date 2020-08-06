package com.sunasterisk.boringweather.ui.current

import androidx.annotation.StringRes
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.CurrentWeather

interface CurrentContract {
    interface View {
        val presenter: Presenter?

        fun showCity(city: City)
        fun showCurrentWeather(currentWeather: CurrentWeather)
        fun showError(@StringRes errorStringRes: Int)
        fun finishRefresh()
    }

    interface Presenter {
        val view: View

        fun loadCityById(cityId: Int)
        fun refreshCurrentWeather(city: City, forceNetwork: Boolean = false)
        fun stopLoadData()
    }
}
