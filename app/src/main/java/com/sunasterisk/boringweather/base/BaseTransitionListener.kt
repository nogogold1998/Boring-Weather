package com.sunasterisk.boringweather.base

import androidx.constraintlayout.motion.widget.MotionLayout

/**
 * for [MotionLayout] transition
 */
abstract class BaseTransitionListener : MotionLayout.TransitionListener {
    override fun onTransitionTrigger(
        layout: MotionLayout,
        triggerId: Int,
        possitive: Boolean,
        progress: Float
    ) {
    }

    override fun onTransitionStarted(
        layout: MotionLayout,
        startState: Int, endState: Int
    ) {
    }

    override fun onTransitionChange(
        layout: MotionLayout,
        startState: Int, endState: Int,
        progress: Float
    ) {
    }

    override fun onTransitionCompleted(
        layout: MotionLayout,
        completedState: Int
    ) {
    }
}
