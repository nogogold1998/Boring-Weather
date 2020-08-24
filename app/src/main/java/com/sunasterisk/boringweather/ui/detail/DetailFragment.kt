package com.sunasterisk.boringweather.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sunasterisk.boringweather.base.BaseDataBindingFragment
import com.sunasterisk.boringweather.base.BaseTransitionListener
import com.sunasterisk.boringweather.base.Single
import com.sunasterisk.boringweather.data.model.DailyWeather
import com.sunasterisk.boringweather.databinding.FragmentDetailBinding
import com.sunasterisk.boringweather.di.NewInjector
import com.sunasterisk.boringweather.ui.current.NavigateToDetailsFragmentRequest
import com.sunasterisk.boringweather.ui.detail.model.DailyWeatherItem
import com.sunasterisk.boringweather.util.TimeUtils
import com.sunasterisk.boringweather.util.defaultSharedPreferences
import com.sunasterisk.boringweather.util.lastCompletelyVisibleItemPosition
import com.sunasterisk.boringweather.util.scrollToPositionScrollChangeListenerAware
import com.sunasterisk.boringweather.util.setupDefaultItemDecoration
import com.sunasterisk.boringweather.util.showToast
import com.sunasterisk.boringweather.util.verticalScrollProgress
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class DetailFragment : BaseDataBindingFragment<FragmentDetailBinding>() {

    private var adapter: DetailWeatherAdapter? = null

    private val pendingRestoreExpandedItem = Single<List<Long>>()

    private val pendingRestoreRecyclerScrollPosition = Single<Int>()

    private val motionListener: BaseTransitionListener by lazy {
        object : BaseTransitionListener() {
            override fun onTransitionCompleted(layout: MotionLayout, completedState: Int) {
                when (completedState) {
                    layout.endState -> {
                        if (!swipeRefreshLayout.isRefreshing) swipeRefreshLayout.isEnabled = false
                    }
                    layout.startState -> swipeRefreshLayout.isEnabled = true
                }
            }
        }
    }

    private val viewModel: DetailViewModel by lazy {
        ViewModelProvider(
            this,
            DetailViewModel.Factory(NewInjector.provideOneCallWeatherRepository(requireContext()))
        ).get(DetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val args = DetailFragmentArgs.fromBundle(it)
            viewModel.loadDetailWeather(
                NavigateToDetailsFragmentRequest(
                    args.cityId,
                    args.dailyWeatherDateTime,
                    args.forcusHourlyWeatherDt
                )
            )
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentDetailBinding.inflate(inflater, container, false).apply {
        viewModel = this@DetailFragment.viewModel
        lifecycleOwner = viewLifecycleOwner
    }

    override fun observeLiveData() {
        viewModel.isRefreshing.observe(viewLifecycleOwner) {
            if (motionDetail.currentState == motionDetail.endState) {
                swipeRefreshLayout.isEnabled = false
            }
        }
        viewModel.errorRes.observe(viewLifecycleOwner) {
            if (it != null) showError(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAppBar()
        setupMotionDetail()
        setupRecyclerViewDetail()
        savedInstanceState?.let(::pendingRestoreRecyclerView)
    }

    private fun pendingRestoreRecyclerView(savedInstanceState: Bundle) = with(savedInstanceState) {
        pendingRestoreExpandedItem.value = getLongArray(KEY_EXPANDED_ITEMS)?.toList()
        pendingRestoreRecyclerScrollPosition.value = getInt(KEY_SCROLL_POSITION)
    }

    private fun setupAppBar() {
        imageUpButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        saveRecyclerViewState(outState)
    }

    override fun onDestroyView() {
        motionDetail.removeTransitionListener(motionListener)
        super.onDestroyView()
    }

    private fun saveRecyclerViewState(outState: Bundle) {
        recyclerViewDetail?.takeUnless { it.verticalScrollProgress == 0f }
            ?.let { outState.putInt(KEY_SCROLL_POSITION, it.lastCompletelyVisibleItemPosition) }
    }

    private fun setupMotionDetail() {
        motionDetail.addTransitionListener(motionListener)
    }

    private fun setupRecyclerViewDetail() = with(recyclerViewDetail) {
        adapter = DetailWeatherAdapter(
            requireContext().defaultSharedPreferences.unitSystem,
            TimeUtils.FORMAT_TIME_SHORT
        ) { recyclerViewDetail.scrollToPositionScrollChangeListenerAware(it) }.also {
            this@DetailFragment.adapter = it
            post { it.submitList(listOf(DailyWeatherItem(DailyWeather.default))) }
        }

        post { if (verticalScrollProgress > 0f) motionDetail?.transitionToEnd() }
        setOnScrollChangeListener { _: View, _: Int, _: Int, _: Int, _: Int ->
            val scrollProgress = verticalScrollProgress
            if (scrollProgress > 0f) motionDetail?.transitionToEnd()
        }

        setupDefaultItemDecoration()
    }

    private fun showError(errorStringRes: Int) {
        context?.showToast(getString(errorStringRes))
    }

    companion object {
        private const val ARGUMENT_CITY_ID = "city_id"
        private const val ARGUMENT_DAILY_WEATHER_DATE_TIME = "dailyWeather_dt"
        private const val ARGUMENT_FOCUS_HOURLY_WEATHER_DATE_TIME = "focus_hourlyWeather_dt"

        private const val KEY_EXPANDED_ITEMS = "expanded_items"
        private const val KEY_SCROLL_POSITION = "scroll_progress"
    }
}
