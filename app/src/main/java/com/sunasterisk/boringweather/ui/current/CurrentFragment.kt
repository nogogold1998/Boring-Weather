package com.sunasterisk.boringweather.ui.current

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.util.TemperatureUnit
import com.sunasterisk.boringweather.util.verticalScrollProgress
import kotlinx.android.synthetic.main.fragment_current.*

class CurrentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_current, container, false)
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        recyclerTodaySummaryWeather.adapter = SummaryWeatherAdapter(TemperatureUnit.CELSIUS)

        recyclerForecastSummaryWeather.adapter = SummaryWeatherAdapter(TemperatureUnit.CELSIUS)

        scrollView.setOnScrollChangeListener { scrollView: NestedScrollView, _: Int, _: Int, _: Int, _: Int ->
            if (!swipeRefreshLayout.isRefreshing) swipeRefreshLayout.isEnabled = false
            if (scrollView.verticalScrollProgress == 0f) swipeRefreshLayout.isEnabled = true
        }

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        swipeRefreshLayout.setOnRefreshListener {
            // TODO implement refresh usecase

            refreshFinish()
        }
    }

    private fun refreshFinish() {
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.isEnabled = scrollView.verticalScrollProgress == 0f
    }

    companion object {
        fun newInstance() = CurrentFragment()
    }
}
