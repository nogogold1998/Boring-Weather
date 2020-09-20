package com.sunasterisk.boringweather.util

import androidx.annotation.StringRes

class LastFetchOutDateException(msg: String? = null) : Exception(msg) {
    @StringRes
    val errStringRes: Int = arrayOf(
        com.sunasterisk.boringweather.R.string.error_refresh_city_not_outdated,
        com.sunasterisk.boringweather.R.string.error_refresh_city_spamming
    ).random()
}
