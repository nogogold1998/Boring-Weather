package com.sunasterisk.boringweather.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.ui.current.CurrentFragment
import com.sunasterisk.boringweather.ui.detail.DetailFragment
import com.sunasterisk.boringweather.ui.search.SearchFragment
import com.sunasterisk.boringweather.ui.setting.SettingsFragment
import com.sunasterisk.boringweather.util.defaultSharedPreferences

class MainActivity : AppCompatActivity(), MainContract.View, Navigator {
    override val containerId: Int get() = R.id.fragment_container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigateStartFragment()
    }

    override fun popBackStack() {
        supportFragmentManager.popBackStack()
    }

    override fun navigateStartFragment() {
        val selectedCityId = defaultSharedPreferences.selectedCityId
        val currentFragment = CurrentFragment.newInstance(selectedCityId)
        supportFragmentManager.beginTransaction()
            .replace(containerId, currentFragment)
            .commit()
    }

    override fun navigateToSearchFragment() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            .replace(containerId, SearchFragment())
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun navigateToDetailsFragment(cityId: Int, dailyWeatherDateTime: Long) {
        beginTransactionWithSlideAnim().replace(
            containerId,
            DetailFragment.newInstance(cityId, dailyWeatherDateTime)
        )
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun navigateToSettingsFragment() {
        beginTransactionWithSlideAnim()
            .replace(containerId, SettingsFragment())
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    private fun beginTransactionWithSlideAnim() = supportFragmentManager.beginTransaction()
        .setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
}
