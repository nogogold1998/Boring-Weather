package com.sunasterisk.boringweather.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.ui.current.CurrentFragment

class MainActivity : AppCompatActivity(), MainContract.View, Navigator {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        onNavigateToFragment(R.id.fragment_container, CurrentFragment.newInstance(), null, null)
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
            // todo add custom animation transition
            // .setCustomAnimations()
            .addToBackStack(backStackName)
            .commit()
    }
}
