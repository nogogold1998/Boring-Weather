package com.sunasterisk.boringweather.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.ui.detail.DetailFragment

class MainActivity : AppCompatActivity(), MainContract.View, Navigator {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment =
            DetailFragment()
            // CurrentFragment.newInstance(/*TODO get cityId from setting or SearchFragment*/)
        onNavigateToFragment(R.id.fragment_container, fragment, null, null)
    }

    override fun onPopBackStack() {
        supportFragmentManager.popBackStack()
    }

    override fun onNavigateToFragment(
        containerId: Int,
        fragment: Fragment,
        tag: String?,
        backStackName: String?
    ) {
        supportFragmentManager.beginTransaction()
            .replace(containerId, fragment, tag)
            // TODO add custom animation transition
            // .setCustomAnimations()
            .addToBackStack(backStackName)
            .commit()
    }
}
