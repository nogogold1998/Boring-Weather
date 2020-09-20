package com.sunasterisk.boringweather.util

import com.sunasterisk.boringweather.data.model.City

val City.isOutDated: Boolean
    get() = TimeUtils.getCurrentInSeconds() > lastFetch + Constants.HOUR_TO_SECONDS
