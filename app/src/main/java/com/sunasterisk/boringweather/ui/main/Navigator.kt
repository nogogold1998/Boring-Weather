package com.sunasterisk.boringweather.ui.main

import androidx.fragment.app.Fragment

interface Navigator {
    fun onPopBackStack()

    fun onNavigateToFragment(containerId: Int, fragment: Fragment, tag: String?, backStackName: String?)
}
