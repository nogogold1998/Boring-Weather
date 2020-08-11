package com.sunasterisk.boringweather.ui.search

import android.location.Location
import androidx.annotation.StringRes
import com.sunasterisk.boringweather.data.model.City

interface SearchContract {

    interface View {
        val presenter: Presenter?

        fun showSearchResult(cities: List<City>)

        fun showError(@StringRes errorStringRes: Int)
    }

    interface Presenter {
        val view: View

        fun searchCity(input: String, limit: Int)

        fun getFetchedCities()

        fun searchCityByLocation(location: Location)
    }
}
