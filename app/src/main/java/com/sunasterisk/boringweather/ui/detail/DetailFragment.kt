package com.sunasterisk.boringweather.ui.detail

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.BaseFragment
import com.sunasterisk.boringweather.base.BaseTransitionListener
import com.sunasterisk.boringweather.base.CallbackAsyncTask
import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.base.Single
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.DailyWeather
import com.sunasterisk.boringweather.data.model.DetailWeather
import com.sunasterisk.boringweather.data.model.HourlyWeather
import com.sunasterisk.boringweather.di.Injector
import com.sunasterisk.boringweather.ui.detail.model.DailyWeatherItem
import com.sunasterisk.boringweather.ui.detail.model.DetailWeatherAdapterItem
import com.sunasterisk.boringweather.ui.detail.model.HourlyWeatherItem
import com.sunasterisk.boringweather.ui.detail.model.LoadDetailWeatherRequest
import com.sunasterisk.boringweather.ui.main.findNavigator
import com.sunasterisk.boringweather.util.TimeUtils
import com.sunasterisk.boringweather.util.defaultSharedPreferences
import com.sunasterisk.boringweather.util.showToast
import com.sunasterisk.boringweather.util.verticalScrollProgress
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : BaseFragment(), DetailContract.View {

    override val layoutResource = R.layout.fragment_detail

    private var cityId = City.default.id

    private var dailyWeatherDateTime: Long = DailyWeather.default.dateTime

    override var presenter: DetailContract.Presenter? = null

    private var adapter: DetailWeatherAdapter? = null

    private val pendingRestoreExpandedItem = Single<List<Long>>()

    private val pendingRestoreRecyclerScrollPosition = Single<Int>()

    private val pendingFocusToHourlyWeatherItemDateTime = Single<Long>()

    private val animTime: Long by lazy {
        resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
    }

    private val actionBarSize: Int by lazy {
        val typedValue = TypedValue()
        return@lazy if (requireContext().theme
                .resolveAttribute(android.R.attr.actionBarSize, typedValue, true)
        ) {
            TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
        } else 0
    }

    private val imageBackgroundScaling: Float by lazy {
        val typedValue = TypedValue()
        resources.getValue(R.dimen.scaling_image_background_end, typedValue, true)
        typedValue.float
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            cityId = it.getInt(ARGUMENT_CITY_ID, cityId)
            dailyWeatherDateTime =
                it.getLong(ARGUMENT_DAILY_WEATHER_DATE_TIME, dailyWeatherDateTime)
            if (savedInstanceState == null)
                pendingFocusToHourlyWeatherItemDateTime.value =
                    it.getLong(ARGUMENT_FOCUS_HOURLY_WEATHER_DATE_TIME)
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

        savedInstanceState?.let(::pendingRestoreRecyclerView)
    }

    private fun pendingRestoreRecyclerView(savedInstanceState: Bundle) = with(savedInstanceState) {
        pendingRestoreExpandedItem.value = getLongArray(KEY_EXPANDED_ITEMS)?.toList()
        pendingRestoreRecyclerScrollPosition.value = getInt(KEY_SCROLL_POSITION)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        adapter?.currentList
            ?.filterIsInstance(HourlyWeatherItem::class.java)
            ?.filter(HourlyWeatherItem::expanded)
            ?.map { it.data.dateTime }
            ?.toLongArray()
            ?.let { outState.putLongArray(KEY_EXPANDED_ITEMS, it) }

        recyclerViewDetail?.takeUnless { it.verticalScrollProgress == 0f }
            ?.let { it.layoutManager as? LinearLayoutManager }
            ?.let {
                outState.putInt(KEY_SCROLL_POSITION, it.findLastCompletelyVisibleItemPosition())
            }
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
        adapter = DetailWeatherAdapter(
            requireContext().defaultSharedPreferences.unitSystem,
            TimeUtils.FORMAT_TIME_SHORT
        ) { recyclerViewDetail.scrollToPosition(it) }.also {
            this@DetailFragment.adapter = it
            post { it.submitList(listOf(DailyWeatherItem(DailyWeather.default))) }
        }

        post { if (verticalScrollProgress > 0f) motionDetail?.transitionToEnd() }
        setOnScrollChangeListener { _: View, _: Int, _: Int, _: Int, _: Int ->
            val scrollProgress = verticalScrollProgress
            if (scrollProgress > 0f) motionDetail?.transitionToEnd()
            translateBackgroundImage(scrollProgress)
        }

        (layoutManager as? LinearLayoutManager)?.orientation
            ?.let { addItemDecoration(DividerItemDecoration(context, it)) }
    }

    private fun translateBackgroundImage(scrollProgress: Float) {
        imageBackground.translationY = (1 - scrollProgress * actionBarSize) * imageBackgroundScaling
    }

    override fun showError(errorStringRes: Int) {
        context?.showToast(getString(errorStringRes))
    }

    override fun showDetailWeather(detailWeather: DetailWeather) = with(detailWeather) {
        showCity(city)
        CallbackAsyncTask<DetailWeather, List<DetailWeatherAdapterItem<*>>>(
            handler = {
                generateDetailWeatherAdapterItemList(it).also(::findFocusItemPosition)
            },
            onFinishedListener = {
                when (it) {
                    is Result.Success -> submitListRecyclerView(it.data)
                    is Result.Error -> showError(R.string.error_show_detail_weather)
                    null -> showError(R.string.error_unknown)
                }
            }
        ).executeOnExecutor(detailWeather)
    }

    override fun finishRefresh() {
        swipeRefreshLayout.isRefreshing = false
        if (motionDetail.currentState == motionDetail.endState) swipeRefreshLayout.isEnabled = false
    }

    override fun showCity(city: City) {
        this.cityId = city.id
        textToolbarTitle.text = city.name
    }

    private fun findFocusItemPosition(list: List<DetailWeatherAdapterItem<*>>) {
        pendingFocusToHourlyWeatherItemDateTime.value?.let { focusDateTime ->
            list.indexOfFirst { (it as? HourlyWeatherItem)?.data?.dateTime == focusDateTime }
                .let {
                    pendingRestoreRecyclerScrollPosition.value = it
                    (list.getOrNull(it) as? HourlyWeatherItem)?.expanded = true
                }
        }
    }

    private fun generateDetailWeatherAdapterItemList(
        detail: DetailWeather
    ): List<DetailWeatherAdapterItem<*>> {
        val expandedMap = pendingRestoreExpandedItem.value
            ?.associateWith { it != HourlyWeather.default.dateTime }
        return listOf(
            DailyWeatherItem(detail.dailyWeather),
            *detail.hourlyWeathers
                .map { HourlyWeatherItem(it, expandedMap?.get(it.dateTime) ?: false) }
                .toTypedArray()
        )
    }

    private fun submitListRecyclerView(items: List<DetailWeatherAdapterItem<*>>) {
        recyclerViewDetail?.postDelayed({
            adapter?.submitList(items) {
                pendingRestoreRecyclerScrollPosition.value
                    ?.let { recyclerViewDetail?.scrollToPosition(it) }
            }
        }, animTime)
    }

    companion object {
        private const val ARGUMENT_CITY_ID = "city_id"
        private const val ARGUMENT_DAILY_WEATHER_DATE_TIME = "dailyWeather_dt"
        private const val ARGUMENT_FOCUS_HOURLY_WEATHER_DATE_TIME = "focus_hourlyWeather_dt"

        private const val KEY_EXPANDED_ITEMS = "expanded_items"
        private const val KEY_SCROLL_POSITION = "scroll_progress"

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
