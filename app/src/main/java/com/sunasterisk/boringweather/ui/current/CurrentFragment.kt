package com.sunasterisk.boringweather.ui.current

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.navigation.NavigationView
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.BaseFragment
import com.sunasterisk.boringweather.base.Single
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.CurrentWeather
import com.sunasterisk.boringweather.data.model.DailyWeather
import com.sunasterisk.boringweather.data.model.HourlyWeather
import com.sunasterisk.boringweather.data.model.SummaryWeather
import com.sunasterisk.boringweather.di.Injector
import com.sunasterisk.boringweather.ui.main.findNavigator
import com.sunasterisk.boringweather.ui.search.SearchConstants
import com.sunasterisk.boringweather.util.DefaultSharedPreferences
import com.sunasterisk.boringweather.util.TimeUtils
import com.sunasterisk.boringweather.util.UnitSystem
import com.sunasterisk.boringweather.util.defaultSharedPreferences
import com.sunasterisk.boringweather.util.firstCompletelyVisibleItemPosition
import com.sunasterisk.boringweather.util.lastCompletelyVisibleItemPosition
import com.sunasterisk.boringweather.util.lazy
import com.sunasterisk.boringweather.util.load
import com.sunasterisk.boringweather.util.setupDefaultItemDecoration
import com.sunasterisk.boringweather.util.showToast
import com.sunasterisk.boringweather.util.verticalScrollProgress
import kotlinx.android.synthetic.main.fragment_current.*
import kotlinx.android.synthetic.main.fragment_current.view.*
import kotlinx.android.synthetic.main.partial_detail.*
import kotlinx.android.synthetic.main.partial_nav_view_header.view.*
import kotlinx.android.synthetic.main.partial_summary.*

class CurrentFragment : BaseFragment(), CurrentContract.View,
    NavigationView.OnNavigationItemSelectedListener,
    DrawerLayout.DrawerListener {

    private var cityId = City.default.id

    private var todayDailyWeather = DailyWeather.default

    private var unitSystem: UnitSystem by lazy { defaultSharedPreferences.unitSystem }

    private var pendingDrawerCloseRunnable = Single<Runnable>()

    private val pendingRestoreTodayRecyclerViewPosition = Single<Int>()

    private val pendingRestoreForecastRecyclerViewPosition = Single<Int>()

    private val defaultSharedPreferences: DefaultSharedPreferences
        by lazy { requireContext().defaultSharedPreferences }

    override val layoutResource = R.layout.fragment_current

    override var presenter: CurrentContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getCityId(savedInstanceState)

        if (cityId == City.default.id) findNavigator()?.navigateToSearchFragment()
        presenter = CurrentPresenter(
            this,
            Injector.getOneCallRepository(requireContext()),
            Injector.getCityRepository(requireContext())
        )

        setFragmentResultListener(SearchConstants.KEY_REQUEST_SEARCH_CITY) { requestKey: String, bundle: Bundle ->
            if (requestKey == SearchConstants.KEY_REQUEST_SEARCH_CITY) {
                cityId = bundle.getInt(SearchConstants.KEY_BUNDLE_CITY_ID)
                presenter?.loadCityById(cityId)
            }
        }
    }

    private fun getCityId(savedInstanceState: Bundle?) {
        cityId = savedInstanceState?.getInt(KEY_CITY_ID)
            ?: arguments?.getInt(ARGUMENT_CITY_ID)
                ?: defaultSharedPreferences.selectedCityId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter?.loadCityById(cityId)
        initViews()
        savedInstanceState?.let(::pendingRestoreRecyclerView)
        navView.getHeaderView(0).imageOpenWeather.load(getString(R.string.url_open_weather_icon)) {
            fitCenter()
        }
    }

    private fun initViews() {
        navView.setNavigationItemSelectedListener(this)
        drawerLayout.addDrawerListener(this)

        initRecyclerViews()

        scrollView.setOnScrollChangeListener { scrollView: NestedScrollView, _: Int, _: Int, _: Int, _: Int ->
            if (!swipeRefreshLayout.isRefreshing) swipeRefreshLayout.isEnabled = false
            if (scrollView.verticalScrollProgress == 0f) swipeRefreshLayout.isEnabled = true
        }

        appbarCurrent.toolbarSearch
            .setNavigationOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        toolbarSearch.setOnMenuItemClickListener {
            if (it.itemId == R.id.searchCity) {
                findNavigator()?.navigateToSearchFragment()
                true
            } else false
        }

        swipeRefreshLayout.setOnRefreshListener {
            presenter?.refreshCurrentWeather(true)
        }
    }

    override fun onStop() {
        super.onStop()
        presenter?.stopLoadData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_CITY_ID, cityId)

        saveRecyclerViewsState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        pendingRestoreTodayRecyclerViewPosition.value =
            recyclerTodaySummaryWeather?.firstCompletelyVisibleItemPosition
        pendingRestoreForecastRecyclerViewPosition.value =
            recyclerForecastSummaryWeather?.firstCompletelyVisibleItemPosition
    }

    private fun initRecyclerViews() {
        recyclerTodaySummaryWeather.adapter =
            SummaryWeatherAdapter(unitSystem, TimeUtils.FORMAT_TIME_SHORT) {
                findNavigator()
                    ?.navigateToDetailsFragment(cityId, todayDailyWeather.dateTime, it.dt)
            }

        recyclerForecastSummaryWeather.adapter =
            SummaryWeatherAdapter(unitSystem, TimeUtils.FORMAT_DATE_SHORT) {
                findNavigator()?.navigateToDetailsFragment(cityId, it.dt)
            }

        recyclerTodaySummaryWeather.setupDefaultItemDecoration()
        recyclerForecastSummaryWeather.setupDefaultItemDecoration()
    }

    private fun pendingRestoreRecyclerView(savedInstanceState: Bundle) = with(savedInstanceState) {
        pendingRestoreTodayRecyclerViewPosition.value = getInt(KEY_TODAY_RECYCLER_POSITION)
        pendingRestoreForecastRecyclerViewPosition.value = getInt(KEY_FORECAST_RECYCLER_POSITION)
    }

    private fun saveRecyclerViewsState(outState: Bundle) {
        (recyclerTodaySummaryWeather?.lastCompletelyVisibleItemPosition
            ?: pendingRestoreTodayRecyclerViewPosition.peek())
            ?.let { outState.putInt(KEY_TODAY_RECYCLER_POSITION, it) }

        (recyclerForecastSummaryWeather?.lastCompletelyVisibleItemPosition
            ?: pendingRestoreForecastRecyclerViewPosition.peek())
            ?.let { outState.putInt(KEY_FORECAST_RECYCLER_POSITION, it) }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.itemCurrent -> true
        R.id.itemForecast -> {
            showError(R.string.error_feature_not_implemented)
            false
        }
        R.id.itemHistory -> {
            showError(R.string.error_feature_not_implemented)
            false
        }
        R.id.itemLocations -> {
            showError(R.string.error_feature_not_implemented)
            false
        }
        R.id.itemSettings -> {
            pendingDrawerCloseRunnable.value =
                Runnable { findNavigator()?.navigateToSettingsFragment() }
            true
        }
        else -> false
    }.also { if (it) drawerLayout.closeDrawers() }

    override fun onDrawerClosed(drawerView: View) {
        pendingDrawerCloseRunnable.value?.run()
    }

    override fun onDrawerStateChanged(newState: Int) = Unit

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) = Unit

    override fun onDrawerOpened(drawerView: View) = Unit

    override fun showCity(city: City) {
        cityId = city.id
        collapsingToolbar.title = city.name
    }

    override fun showCurrentWeather(currentWeather: CurrentWeather) {
        if (isCurrentWeatherIsOutdated(currentWeather)) {
            presenter?.refreshCurrentWeather(true)
                ?.also { swipeRefreshLayout.isRefreshing = true }
            return
        }
        val animTime = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        view?.postDelayed({
            val (city, current, day, todaySummary, forecastSummary) = currentWeather
            city.takeIf { it.id != City.default.id }?.let(this::showCity)
            current.takeIf { it.dateTime != HourlyWeather.default.dateTime }
                ?.let(this::showCurrentHourlyWeather)
            day.takeIf { it.dateTime != DailyWeather.default.dateTime }
                ?.let(this::showDailyWeather)
            showTodaySummaryWeather(todaySummary)
            showForecastSummaryWeather(forecastSummary)
            finishRefresh()
        }, animTime)
    }

    override fun showError(errorStringRes: Int) {
        requireContext().showToast(getString(errorStringRes), Toast.LENGTH_LONG)
        finishRefresh()
    }

    private fun isCurrentWeatherIsOutdated(currentWeather: CurrentWeather) =
        currentWeather.copy(city = City.default) == CurrentWeather.default

    private fun showCurrentHourlyWeather(currentWeather: HourlyWeather) = with(currentWeather) {
        textDateTime.text =
            TimeUtils.formatToString(TimeUtils.FORMAT_DATE_LONG_TIME_SHORT_, dateTime)
        textCurrentTemperature.text = unitSystem.formatTemperature(temperature, resources)
        textFeelsLike.text = unitSystem.formatTemperature(feelsLike, resources)
        textWeatherDescription.text =
            weathers.getOrNull(0)?.description ?: getString(R.string.title_holder_description)
        textVisibility.text = unitSystem.formatDistance(visibility, resources)
        textWindSpeed.text = unitSystem.formatSpeed(windSpeed, resources)
        textUVIndex.text = uvIndex?.toString() ?: getString(R.string.title_holder_float_number)
        textPressure.text = unitSystem.formatPressure(pressure, resources)
        textCloud.text = getString(R.string.format_percent_decimal, clouds)
        textHumidity.text = getString(R.string.format_percent_decimal, humidity)
    }

    private fun showDailyWeather(dailyWeather: DailyWeather) = with(dailyWeather) {
        todayDailyWeather = dailyWeather
        textSunrise.text = TimeUtils.formatToString(TimeUtils.FORMAT_TIME_SHORT, sunrise)
        textDayTemperature.text = unitSystem.formatTemperature(temperature.day, resources)
        textSunset.text = TimeUtils.formatToString(TimeUtils.FORMAT_TIME_SHORT, sunset)
        textNightTemperature.text =
            unitSystem.formatTemperature(temperature.night, resources)
    }

    private fun showTodaySummaryWeather(todaySummaryWeathers: List<SummaryWeather>) {
        (recyclerTodaySummaryWeather.adapter as? SummaryWeatherAdapter)
            ?.submitList(todaySummaryWeathers) {
                pendingRestoreTodayRecyclerViewPosition.value
                    ?.let { recyclerTodaySummaryWeather.scrollToPosition(it) }
            }
    }

    private fun showForecastSummaryWeather(forecastSummaryWeathers: List<SummaryWeather>) {
        (recyclerForecastSummaryWeather.adapter as? SummaryWeatherAdapter)
            ?.submitList(forecastSummaryWeathers) {
                pendingRestoreForecastRecyclerViewPosition.value
                    ?.let { recyclerForecastSummaryWeather?.scrollToPosition(it) }
            }
    }

    private fun finishRefresh() {
        requireView().swipeRefreshLayout.post {
            swipeRefreshLayout.isRefreshing = false
            swipeRefreshLayout.isEnabled = scrollView.verticalScrollProgress == 0f
        }
    }

    companion object {
        private const val KEY_TODAY_RECYCLER_POSITION = "today_recycler_pos"
        private const val KEY_FORECAST_RECYCLER_POSITION = "forecast_recycler_pos"
        private const val KEY_CITY_ID = "city_id"

        private const val ARGUMENT_CITY_ID = "arg_city_id"

        fun newInstance(cityId: Int? = null) = CurrentFragment().apply {
            arguments = Bundle().apply { if (cityId != null) putInt(ARGUMENT_CITY_ID, cityId) }
        }
    }
}
