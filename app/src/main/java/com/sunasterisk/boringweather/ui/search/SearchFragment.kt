package com.sunasterisk.boringweather.ui.search

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.BaseDataBindingFragment
import com.sunasterisk.boringweather.base.Single
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.databinding.FragmentSearchBinding
import com.sunasterisk.boringweather.di.NewInjector
import com.sunasterisk.boringweather.ui.main.findNavigator
import com.sunasterisk.boringweather.util.defaultSharedPreferences
import com.sunasterisk.boringweather.util.lastCompletelyVisibleItemPosition
import com.sunasterisk.boringweather.util.locationManager
import com.sunasterisk.boringweather.util.setupDefaultItemDecoration
import com.sunasterisk.boringweather.util.showSoftInput
import com.sunasterisk.boringweather.util.showToast
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class SearchFragment : BaseDataBindingFragment<FragmentSearchBinding>() {

    @ExperimentalCoroutinesApi
    private val searchViewModel by viewModels<SearchViewModel> {
        val context = requireContext()
        SearchViewModel.Factory(
            NewInjector.provideCityRepository(context),
            context.defaultSharedPreferences
        )
    }

    private val defaultSharedPreferences by lazy { requireContext().defaultSharedPreferences }

    private val pendingRestoreRecyclerViewPosition = Single<Int>()

    private val cityAdapter: CityAdapter by lazy {
        CityAdapter(
            onclickListener = {
                defaultSharedPreferences.selectedCityId = it.id
            },
            onBookMarkListener = {
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.getInt(KEY_RECYCLER_POSITION)
            ?.let { pendingRestoreRecyclerViewPosition.value = it }
    }

    @ExperimentalCoroutinesApi
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false).also {
            it.searchViewModel = searchViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        recyclerSearchCity?.lastCompletelyVisibleItemPosition
            ?.let { outState.putInt(KEY_RECYCLER_POSITION, it) }
    }

    private fun initView() {
        with(binding.recyclerSearchCity) {
            adapter = cityAdapter
            setupDefaultItemDecoration()
        }
    }

    private fun initListener() = with(binding) {
        textInputLayoutSearch.setStartIconOnClickListener {
            if (defaultSharedPreferences.selectedCityId != City.default.id) {
                findNavigator()?.popBackStack()
            } else {
                showError(R.string.error_search_need_select_city)
            }
        }

        textInputLayoutSearch.setEndIconOnClickListener {
            editTextSearch.takeUnless { it.text.isNullOrEmpty() }?.run { text = null }
                ?: searchUsingDeviceLocation()
        }
    }

    // TODO add in onViewCreated
    private fun requestInputFocus(view: View) {
        view.requestFocus()
        context?.showSoftInput(view)
    }

    fun showError(errorStringRes: Int) {
        context?.showToast(getString(errorStringRes))
    }

    private fun searchUsingDeviceLocation() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
                if (isGranted.all { it.value }) {
                    performSearchUsingDeviceLocation()
                } else {
                    showError(R.string.error_search_location_permission_denied)
                }
            }
        when {
            allRequestedPermissionsGranted() -> performSearchUsingDeviceLocation()
            requestedPermissions.any(::shouldShowRequestPermissionRationale) -> {
                val title = getString(R.string.title_search_location_dialog)
                val msg = getString(
                    R.string.format_msg_search_location_dialog_location_require,
                    requestedPermissions.joinToString(", ")
                )
                showLocationDialog(title, msg) {
                    setPositiveButton(android.R.string.yes) { _, _ ->
                        requestPermissionLauncher.launch(requestedPermissions)
                    }
                }
            }
            else -> requestPermissionLauncher.launch(requestedPermissions)
        }
    }

    private fun allRequestedPermissionsGranted() = requestedPermissions.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showLocationDialog(
        title: String,
        msg: String,
        option: (AlertDialog.Builder.() -> Unit)?
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(msg).setNegativeButton(android.R.string.no, null)
            .setIcon(R.drawable.ic_round_location_24)
            .also { option?.invoke(it) }
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun performSearchUsingDeviceLocation() {
        context?.locationManager?.let {
            if (LocationManagerCompat.isLocationEnabled(it)) {
                // TODO Implement later: Show Dialog or navigate to another fragment
            } else {
                val title = getString(R.string.title_search_location_dialog)
                val msg = getString(R.string.msg_search_location_dialog_location_off)
                showLocationDialog(title, msg) {
                    setPositiveButton(R.string.action_go_to_settings) { _, _ ->
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                }
            }
        }
    }

    private fun sendFragmentResult(cityId: Int) = setFragmentResult(
        SearchConstants.KEY_REQUEST_SEARCH_CITY,
        bundleOf(SearchConstants.KEY_BUNDLE_CITY_ID to cityId)
    )

    companion object {
        private const val KEY_RECYCLER_POSITION = "recycler_pos"

        private val requestedPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}
