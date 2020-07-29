package com.sunasterisk.boringweather.ui.current

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import com.google.android.material.navigation.NavigationView
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.BaseFragment
import com.sunasterisk.boringweather.data.CityRepositoryImpl
import com.sunasterisk.boringweather.data.CurrentRepositoryImpl
import com.sunasterisk.boringweather.data.local.model.City
import com.sunasterisk.boringweather.data.local.model.DailyWeather
import com.sunasterisk.boringweather.data.local.model.HourlyWeather
import com.sunasterisk.boringweather.data.local.model.SummaryWeather
import com.sunasterisk.boringweather.util.UnitSystem
import com.sunasterisk.boringweather.util.showToast
import com.sunasterisk.boringweather.util.verticalScrollProgress
import kotlinx.android.synthetic.main.fragment_current.*
import kotlinx.android.synthetic.main.fragment_current.view.*
import kotlinx.android.synthetic.main.tile_detail.*
import kotlinx.android.synthetic.main.tile_summary.*

class CurrentFragment : BaseFragment(), CurrentContract.View,
    NavigationView.OnNavigationItemSelectedListener {
    private var city: City = City()

    private var unitSystem = UnitSystem.METRIC // TODO injected from settings

    override val layoutResource = R.layout.fragment_current

    override var presenter: CurrentContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getInt(ARGUMENT_CITY_ID)?.let { city = City(id = it) }

        presenter = CurrentPresenter(this, CurrentRepositoryImpl(), CityRepositoryImpl())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter?.loadCityById(city.id)
        initView()
    }

    private fun initView() {
        navView.setNavigationItemSelectedListener(this)

        recyclerTodaySummaryWeather.adapter = SummaryWeatherAdapter(unitSystem) {
            // TODO navigate to DetailFragment
        }

        recyclerForecastSummaryWeather.adapter = SummaryWeatherAdapter(unitSystem) {
            // TODO navigate to ForecastFragment
        }

        scrollView.setOnScrollChangeListener { scrollView: NestedScrollView, _: Int, _: Int, _: Int, _: Int ->
            if (!swipeRefreshLayout.isRefreshing) swipeRefreshLayout.isEnabled = false
            if (scrollView.verticalScrollProgress == 0f) swipeRefreshLayout.isEnabled = true
        }

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
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
        presenter?.refreshCurrentWeather(city, false)
    }

    override fun showCurrentWeather(currentWeather: HourlyWeather) = with(currentWeather) {
        textDateTime.text = dateTime.toString() // TODO convert Long to String Date Time
        textCurrentTemperature.text = unitSystem.formatTemperature(temperature, resources)
        textFeelsLike.text = unitSystem.formatTemperature(feelsLike, resources)
        textWeatherDescription.text =
            weathers.getOrNull(0)?.description ?: getString(R.string.title_holder_description)
        textVisibility.text = unitSystem.formatDistance(visibility, resources)
        textWindSpeed.text = unitSystem.formatSpeed(windSpeed, resources)
        textUVIndex.text = uvIndex?.toString() ?: getString(R.string.title_holder_number)
        textPressure.text = unitSystem.formatPressure(pressure, resources)
        textCloud.text = getString(R.string.format_percent_decimal, clouds)
        textHumidity.text = getString(R.string.format_percent_decimal, humidity)
    }

    override fun showDailyWeather(dailyWeather: DailyWeather) = with(dailyWeather) {
        textSunrise.text = sunrise.toString()
        textDayTemperature.text = unitSystem.formatTemperature(temperature.day, resources)
        textSunset.text = sunset.toString()
        textNightTemperature.text =
            unitSystem.formatTemperature(temperature.night, resources)
    }

    override fun showTodaySummaryWeather(todaySummaryWeathers: List<SummaryWeather>) {
        (recyclerTodaySummaryWeather.adapter as? SummaryWeatherAdapter)
            ?.submitList(todaySummaryWeathers)
    }

    override fun showForecastSummaryWeather(forecastSummaryWeathers: List<SummaryWeather>) {
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
            if (cityId != null) arguments?.putInt(ARGUMENT_CITY_ID, cityId)
        }

        private const val ARGUMENT_CITY_ID = "city_id"
    }
}
