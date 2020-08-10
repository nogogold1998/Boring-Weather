package com.sunasterisk.boringweather.ui.main

import androidx.fragment.app.Fragment

interface Navigator {
    val containerId: Int

    fun popBackStack()

    fun navigateStartFragment()

    fun navigateToSearchFragment()

    fun navigateToDetailsFragment(
        cityId: Int,
        dailyWeatherDateTime: Long,
        focusHourlyWeatherDateTime: Long? = null
    )

    fun navigateToSettingsFragment()
}

fun Fragment.findNavigator() = requireActivity() as? Navigator
