package com.sunasterisk.boringweather.ui.current

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.navigation.NavigationView
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.BaseFragment
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.CurrentWeather
import com.sunasterisk.boringweather.data.model.DailyWeather
import com.sunasterisk.boringweather.data.model.HourlyWeather
import com.sunasterisk.boringweather.data.model.SummaryWeather
import com.sunasterisk.boringweather.di.Injector
import com.sunasterisk.boringweather.ui.main.findNavigator
import com.sunasterisk.boringweather.ui.search.SearchConstants
import com.sunasterisk.boringweather.util.TimeUtils
import com.sunasterisk.boringweather.util.UnitSystem
import com.sunasterisk.boringweather.util.showToast
import com.sunasterisk.boringweather.util.verticalScrollProgress
import kotlinx.android.synthetic.main.fragment_current.*
import kotlinx.android.synthetic.main.fragment_current.view.*
import kotlinx.android.synthetic.main.partial_detail.*
import kotlinx.android.synthetic.main.partial_summary.*

class CurrentFragment : BaseFragment(), CurrentContract.View,
    NavigationView.OnNavigationItemSelectedListener {
    private var city = City.default

    private var todayDailyWeather = DailyWeather.default

    private var unitSystem = UnitSystem.METRIC // TODO injected from settings

    override val layoutResource = R.layout.fragment_current

    override var presenter: CurrentContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getInt(ARGUMENT_CITY_ID)?.let { city = City.default.copy(id = it) }

        presenter = CurrentPresenter(
            this,
            Injector.getOneCallRepository(requireContext()),
            Injector.getCityRepository(requireContext())
        )

        setFragmentResultListener(SearchConstants.KEY_REQUEST_SEARCH_CITY) { requestKey: String, bundle: Bundle ->
            if (requestKey == SearchConstants.KEY_REQUEST_SEARCH_CITY) {
                val cityId = bundle.getInt(SearchConstants.KEY_BUNDLE_CITY_ID)
                presenter?.loadCityById(cityId)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter?.loadCityById(city.id)
        initViews()
    }

    private fun initViews() {
        navView.setNavigationItemSelectedListener(this)

        recyclerTodaySummaryWeather.adapter =
            SummaryWeatherAdapter(unitSystem, TimeUtils.FORMAT_TIME_SHORT) {
                findNavigator()?.navigateToDetailsFragment(city.id, todayDailyWeather.dateTime)
            }

        recyclerForecastSummaryWeather.adapter =
            SummaryWeatherAdapter(unitSystem, TimeUtils.FORMAT_DATE_SHORT) {
                // TODO navigate to ForecastFragment
            }

        scrollView.setOnScrollChangeListener { scrollView: NestedScrollView, _: Int, _: Int, _: Int, _: Int ->
            if (!swipeRefreshLayout.isRefreshing) swipeRefreshLayout.isEnabled = false
            if (scrollView.verticalScrollProgress == 0f) swipeRefreshLayout.isEnabled = true
        }

        appbarCurrent.toolbarSearch.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        toolbarSearch.setOnMenuItemClickListener {
            if (it.itemId == R.id.searchCity) {
                findNavigator()?.navigateToSearchFragment()
                true
            }
            else false
        }

        swipeRefreshLayout.setOnRefreshListener {
            presenter?.refreshCurrentWeather(city, true) // TODO check for network state
        }
    }

    override fun onStop() {
        super.onStop()
        presenter?.stopLoadData()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.itemCurrent -> {
            drawerLayout.closeDrawers()
            true
        }
        R.id.itemForecast -> {
            // TODO navigate to ForecastFragment
            true
        }
        R.id.itemHistory -> {
            // TODO navigate to HistoryFragment
            true
        }
        R.id.itemLocations -> {
            // TODO navigate to LocationsFragment
            true
        }
        R.id.itemSettings -> {
            // TODO navigate to SettingsFragment
            true
        }
        else -> false
    }

    override fun showCity(city: City) {
        this.city = city
        collapsingToolbar.title = city.name
    }

    override fun showCurrentWeather(currentWeather: CurrentWeather) {
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
        }, animTime)
    }

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
            ?.submitList(todaySummaryWeathers)
    }

    private fun showForecastSummaryWeather(forecastSummaryWeathers: List<SummaryWeather>) {
        (recyclerForecastSummaryWeather.adapter as? SummaryWeatherAdapter)
            ?.submitList(forecastSummaryWeathers)
    }

    override fun showError(errorStringRes: Int) {
        val errorMsg = getString(errorStringRes)
        requireContext().showToast(errorMsg, Toast.LENGTH_LONG)
    }

    override fun finishRefresh() {
        requireView().swipeRefreshLayout.post {
            swipeRefreshLayout.isRefreshing = false
            swipeRefreshLayout.isEnabled = scrollView.verticalScrollProgress == 0f
        }
    }

    companion object {

        fun newInstance(cityId: Int? = null) = CurrentFragment().apply {
            arguments = Bundle().apply { if (cityId != null) putInt(ARGUMENT_CITY_ID, cityId) }
        }

        private const val ARGUMENT_CITY_ID = "city_id"
    }
}
