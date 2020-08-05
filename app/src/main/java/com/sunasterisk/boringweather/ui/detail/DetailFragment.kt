package com.sunasterisk.boringweather.ui.detail

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.BaseFragment
import com.sunasterisk.boringweather.base.BaseTransitionListener
import com.sunasterisk.boringweather.util.verticalScrollProgress
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : BaseFragment() {

    override val layoutResource = R.layout.fragment_detail

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMotionDetail()
        setupSwipeRefreshLayout()
        setupRecyclerViewDetail()
    }

    private fun setupMotionDetail() {
        motionDetail.addTransitionListener(object : BaseTransitionListener() {
            override fun onTransitionCompleted(layout: MotionLayout, completedState: Int) {
                when (completedState) {
                    layout.endState -> {
                        if (!swipeRefreshLayout.isRefreshing)
                            swipeRefreshLayout.isEnabled = false
                    }
                    layout.startState -> {
                        swipeRefreshLayout.isEnabled = true
                    }
                }
            }
        })
    }

    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener {
            finishRefresh()
        }
    }

    private fun setupRecyclerViewDetail() = with(recyclerViewDetail) {
        adapter = DetailWeatherAdapter { recyclerViewDetail.scrollToPosition(it) }

        post { if (verticalScrollProgress > 0f) motionDetail.transitionToEnd() }
        setOnScrollChangeListener { _: View, _: Int, _: Int, _: Int, _: Int ->
            val scrollProgress = verticalScrollProgress
            if (scrollProgress > 0f) motionDetail.transitionToEnd()

            translateBackgroundImage(scrollProgress)
        }
    }

    private fun finishRefresh() {
        swipeRefreshLayout.isRefreshing = false
        if (motionDetail.currentState == motionDetail.endState) swipeRefreshLayout.isEnabled = false
    }

    private fun translateBackgroundImage(scrollProgress: Float) {
        val typedValueActionBarSize = TypedValue()
        val typedValueBackgroundImageScale = TypedValue()
        if (requireContext().theme.resolveAttribute(
                android.R.attr.actionBarSize,
                typedValueActionBarSize,
                true
            )
        ) {
            val actionBarSize = TypedValue.complexToDimensionPixelSize(
                typedValueActionBarSize.data,
                resources.displayMetrics
            )
            resources.getValue(
                R.dimen.scaling_image_background_end,
                typedValueBackgroundImageScale,
                true
            )
            val imageScaling = typedValueBackgroundImageScale.float
            imageBackground.translationY = (1 - scrollProgress * actionBarSize) * imageScaling
        }
    }

    companion object {
        private const val ARGUMENT_CITY_ID = "city_id"
        private const val ARGUMENT_DAILY_WEATHER_DATE_TIME = "dailyWeather_dt"

        fun newInstance(cityId: Int, dailyWeatherDateTime: Long) = DetailFragment().apply {
            arguments = bundleOf(
                ARGUMENT_CITY_ID to cityId,
                ARGUMENT_DAILY_WEATHER_DATE_TIME to dailyWeatherDateTime
            )
        }
    }
}
