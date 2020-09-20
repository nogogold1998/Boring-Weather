package com.sunasterisk.boringweather.base

import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

// DISCLAIMER: not written by me, source from here
// https://stackoverflow.com/questions/31682310/android-collapsingtoolbarlayout-collapse-listener
abstract class AppbarStateChangeListener : AppBarLayout.OnOffsetChangedListener {
    enum class State { EXPANDED, COLLAPSED, IDLE }

    private var currentState: State = State.IDLE

    override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
        currentState = when {
            i == 0 -> {
                if (currentState != State.EXPANDED) onStateChanged(appBarLayout, State.EXPANDED)
                State.EXPANDED
            }
            abs(i) >= appBarLayout.totalScrollRange -> {
                if (currentState != State.COLLAPSED) onStateChanged(appBarLayout, State.COLLAPSED)
                State.COLLAPSED
            }
            else -> {
                if (currentState != State.IDLE) onStateChanged(appBarLayout, State.IDLE)
                State.IDLE
            }
        }
    }

    abstract fun onStateChanged(appBarLayout: AppBarLayout?, state: State?)
}
