package com.sunasterisk.boringweather.ui.main

import androidx.fragment.app.Fragment

interface Navigator {
    val containerId: Int

    fun popBackStack()

    fun onNavigateToFragment(containerId: Int, fragment: Fragment, tag: String?, backStackName: String?)

    fun navigateStartFragment()

    fun navigateToSearchFragment()

    fun navigateToDetailsFragment(cityId: Int, dailyWeatherDateTime: Long)

    fun navigateToSettingsFragment()
}

fun Fragment.findNavigator() = requireActivity() as? Navigator
