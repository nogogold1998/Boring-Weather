package com.sunasterisk.boringweather.ui.detail

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.BaseFragment
import com.sunasterisk.boringweather.base.BaseTransitionListener
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.DailyWeather
import com.sunasterisk.boringweather.data.model.DetailWeather
import com.sunasterisk.boringweather.di.Injector
import com.sunasterisk.boringweather.ui.detail.model.LoadDetailWeatherRequest
import com.sunasterisk.boringweather.ui.main.findNavigator
import com.sunasterisk.boringweather.util.showToast
import com.sunasterisk.boringweather.util.verticalScrollProgress
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : BaseFragment(), DetailContract.View {

    override val layoutResource = R.layout.fragment_detail

    private var cityId = City.default.id

    private var dailyWeatherDateTime: Long = DailyWeather.default.dateTime

    override var presenter: DetailContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            cityId = it.getInt(ARGUMENT_CITY_ID, cityId)
            dailyWeatherDateTime =
                it.getLong(ARGUMENT_DAILY_WEATHER_DATE_TIME, dailyWeatherDateTime)
        }

        presenter = DetailPresenter(
            this,
            Injector.getOneCallRepository(requireContext()),
            Injector.getCityRepository(requireContext())
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAppBar()
        setupMotionDetail()
        setupSwipeRefreshLayout()
        setupRecyclerViewDetail()
        presenter?.loadDetailWeather(LoadDetailWeatherRequest(cityId, dailyWeatherDateTime))
    }

    private fun setupAppBar() {
        imageUpButton.setOnClickListener {
            findNavigator()?.popBackStack()
        }
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
            presenter?.loadDetailWeather(
                LoadDetailWeatherRequest(cityId, dailyWeatherDateTime, true)
            )
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

    override fun finishRefresh() {
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

    override fun showError(errorStringRes: Int) {
        context?.showToast(getString(errorStringRes))
    }

    override fun showDetailWeather(detailWeather: DetailWeather) {
        showCity(detailWeather.city)
    }

    override fun showCity(city: City) {
        this.cityId = city.id
    }

    companion object {
        private const val ARGUMENT_CITY_ID = "city_id"
        private const val ARGUMENT_DAILY_WEATHER_DATE_TIME = "dailyWeather_dt"
        private const val ARGUMENT_FOCUS_HOURLY_WEATHER_DATE_TIME = "focus_hourlyWeather_dt"

        fun newInstance(
            cityId: Int,
            dailyWeatherDateTime: Long,
            focusHourlyWeatherDateTime: Long? = null
        ) = DetailFragment().apply {
            arguments = bundleOf(
                ARGUMENT_CITY_ID to cityId,
                ARGUMENT_DAILY_WEATHER_DATE_TIME to dailyWeatherDateTime,
                ARGUMENT_FOCUS_HOURLY_WEATHER_DATE_TIME to focusHourlyWeatherDateTime
            )
        }
    }
}
