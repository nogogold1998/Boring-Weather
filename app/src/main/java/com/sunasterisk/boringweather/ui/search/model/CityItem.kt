package com.sunasterisk.boringweather.ui.search.model

import com.sunasterisk.boringweather.data.model.City

data class CityItem(val data: City, val isBookMarked: Boolean = false)

val City.isFetched: Boolean
    get() = lastFetch > City.default.lastFetch
