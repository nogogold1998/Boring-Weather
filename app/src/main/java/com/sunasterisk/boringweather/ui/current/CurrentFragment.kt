package com.sunasterisk.boringweather.ui.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.AppbarStateChangeListener
import com.sunasterisk.boringweather.base.BaseDataBindingFragment
import com.sunasterisk.boringweather.base.Single
import com.sunasterisk.boringweather.base.observeEvent
import com.sunasterisk.boringweather.databinding.FragmentCurrentBinding
import com.sunasterisk.boringweather.di.NewInjector
import com.sunasterisk.boringweather.util.DefaultSharedPreferences
import com.sunasterisk.boringweather.util.TimeUtils
import com.sunasterisk.boringweather.util.UnitSystem
import com.sunasterisk.boringweather.util.defaultSharedPreferences
import com.sunasterisk.boringweather.util.lazy
import com.sunasterisk.boringweather.util.load
import com.sunasterisk.boringweather.util.setupDefaultItemDecoration
import com.sunasterisk.boringweather.util.showToast
import com.sunasterisk.boringweather.util.verticalScrollProgress
import kotlinx.android.synthetic.main.fragment_current.*
import kotlinx.android.synthetic.main.fragment_current.view.*
import kotlinx.android.synthetic.main.partial_nav_view_header.view.*

class CurrentFragment : BaseDataBindingFragment<FragmentCurrentBinding>(),
    NavigationView.OnNavigationItemSelectedListener,
    DrawerLayout.DrawerListener {

    private val viewModel: CurrentViewModel by activityViewModels {
        CurrentViewModel.Factory(
            NewInjector.provideOneCallWeatherRepository(requireContext()),
            requireContext().defaultSharedPreferences
        )
    }

    private val defaultSharedPreferences: DefaultSharedPreferences
        by lazy { requireContext().defaultSharedPreferences }
    private val unitSystem: UnitSystem by lazy { defaultSharedPreferences.unitSystem }

    private val pendingDrawerCloseRunnable = Single<Runnable>()
    private val pendingRestoreTodayRecyclerViewPosition = Single<Int>()
    private val pendingRestoreForecastRecyclerViewPosition = Single<Int>()
    private var expandedCollapsingToolbar = true

    private val appbarStateChangeListener = object : AppbarStateChangeListener() {
        override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
            when (state) {
                State.EXPANDED -> {
                    swipeRefreshLayout.isEnabled = true
                    expandedCollapsingToolbar = true
                }
                else -> {
                    if (!swipeRefreshLayout.isRefreshing) swipeRefreshLayout.isEnabled = false
                    if (state == State.COLLAPSED) expandedCollapsingToolbar = false
                }
            }
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) =
        FragmentCurrentBinding.inflate(inflater, container, false).also {
            it.viewModel = viewModel
            it.unitSystem = defaultSharedPreferences.unitSystem
            it.lifecycleOwner = viewLifecycleOwner
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(savedInstanceState)
        savedInstanceState?.let(::pendingRestoreRecyclerView)
    }

    override fun observeLiveData() {
        viewModel.errorRes.observe(viewLifecycleOwner) { it?.let(this::showError) }
        viewModel.navigationEvent.observeEvent(viewLifecycleOwner) { action ->
            findNavController().navigate(action)
        }
    }

    private fun initViews(savedInstanceState: Bundle?) = with(binding) {
        navView.getHeaderView(0).imageOpenWeather
            .load(getString(R.string.url_open_weather_icon)) {
                fitCenter()
            }
        navView.setNavigationItemSelectedListener(this@CurrentFragment)
        drawerLayout.addDrawerListener(this@CurrentFragment)

        initRecyclerViews()

        scrollView.setOnScrollChangeListener { scrollView: NestedScrollView, _: Int, _: Int, _: Int, _: Int ->
            if (!swipeRefreshLayout.isRefreshing) swipeRefreshLayout.isEnabled = false
            if (scrollView.verticalScrollProgress == 0f) swipeRefreshLayout.isEnabled = true
        }
        with(appbarCurrent) {
            post {
                val isExpanded =
                    savedInstanceState?.getBoolean(KEY_EXPANDED_COLLAPSING_TOOL_BAR)
                        ?: expandedCollapsingToolbar
                setExpanded(isExpanded)
                expandedCollapsingToolbar = isExpanded

                addOnOffsetChangedListener(appbarStateChangeListener)
            }
        }
        appbarCurrent.toolbarSearch.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        toolbarSearch.setOnMenuItemClickListener {
            if (it.itemId == R.id.searchCity) {
                findNavController().navigate(CurrentFragmentDirections.actionCurrentToSearch())
                true
            } else {
                false
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(KEY_EXPANDED_COLLAPSING_TOOL_BAR, expandedCollapsingToolbar)
        /* FIXME configuration changes
            single fragment: onSaveInstanceState -> onDestroyView
            fragment A in foreground, fragment B in back stack
            fragment B onDestroyView invoked when it's replaced by A
            but onSaveInstanceState then later be invoked due to its host activity saveState
        */
        // with(binding) {
        //     recyclerTodaySummaryWeather.centerItemPosition
        //         .let { outState.putInt(KEY_TODAY_RECYCLER_POSITION, it) }
        //     recyclerForecastSummaryWeather.centerItemPosition
        //         .let { outState.putInt(KEY_FORECAST_RECYCLER_POSITION, it) }
        // }
    }

    override fun onDestroyView() {
        binding.appbarCurrent.removeOnOffsetChangedListener(appbarStateChangeListener)
        binding.drawerLayout.removeDrawerListener(this)
        super.onDestroyView()
    }

    private fun initRecyclerViews() = with(binding) {
        recyclerTodaySummaryWeather.adapter =
            SummaryWeatherAdapter(this@CurrentFragment.unitSystem, TimeUtils.FORMAT_TIME_SHORT) {
                this@CurrentFragment.viewModel.navigateToDetailsFragment(it.dt, true)
            }

        recyclerForecastSummaryWeather.adapter =
            SummaryWeatherAdapter(this@CurrentFragment.unitSystem, TimeUtils.FORMAT_DATE_SHORT) {
                this@CurrentFragment.viewModel.navigateToDetailsFragment(it.dt, false)
            }

        recyclerTodaySummaryWeather.setupDefaultItemDecoration()
        recyclerForecastSummaryWeather.setupDefaultItemDecoration()
    }

    private fun pendingRestoreRecyclerView(savedInstanceState: Bundle) = with(savedInstanceState) {
        pendingRestoreTodayRecyclerViewPosition.value = getInt(KEY_TODAY_RECYCLER_POSITION)
        pendingRestoreForecastRecyclerViewPosition.value = getInt(KEY_FORECAST_RECYCLER_POSITION)
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
            findNavController().navigate(CurrentFragmentDirections.actionCurrentToSettings())
            true
        }
        else -> false
    }

    override fun onDrawerClosed(drawerView: View) {
        pendingDrawerCloseRunnable.value?.run()
    }

    override fun onDrawerStateChanged(newState: Int) = Unit

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) = Unit

    override fun onDrawerOpened(drawerView: View) = Unit

    private fun showError(errorStringRes: Int) {
        requireContext().showToast(getString(errorStringRes), Toast.LENGTH_LONG)
    }

    companion object {
        private const val KEY_TODAY_RECYCLER_POSITION = "today_recycler_pos"
        private const val KEY_FORECAST_RECYCLER_POSITION = "forecast_recycler_pos"
        private const val KEY_EXPANDED_COLLAPSING_TOOL_BAR = "expanded_collapsing_tool_bar"

        const val CITY_ID = "city_id"
    }
}
