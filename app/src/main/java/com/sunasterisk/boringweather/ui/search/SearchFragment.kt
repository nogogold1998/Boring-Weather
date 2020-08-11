package com.sunasterisk.boringweather.ui.search

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.BaseFragment
import com.sunasterisk.boringweather.base.CallbackAsyncTask
import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.base.Single
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.di.Injector
import com.sunasterisk.boringweather.ui.main.findNavigator
import com.sunasterisk.boringweather.ui.search.model.CityItem
import com.sunasterisk.boringweather.util.defaultSharedPreferences
import com.sunasterisk.boringweather.util.lastCompletelyVisibleItemPosition
import com.sunasterisk.boringweather.util.locationManager
import com.sunasterisk.boringweather.util.networkState
import com.sunasterisk.boringweather.util.setupDefaultItemDecoration
import com.sunasterisk.boringweather.util.showSoftInput
import com.sunasterisk.boringweather.util.showToast
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment(), SearchContract.View {
    override val layoutResource: Int get() = R.layout.fragment_search

    override var presenter: SearchContract.Presenter? = null

    private val defaultSharedPreferences by lazy { requireContext().defaultSharedPreferences }

    private val pendingRestoreRecyclerViewPosition = Single<Int>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val cityAdapter: CityAdapter by lazy {
        CityAdapter(
            onclickListener = {
                if (defaultSharedPreferences.selectedCityId == City.default.id) {
                    toggleSelectedCity(it)
                }
                sendFragmentResult(it)
                findNavigator()?.popBackStack()
            },
            onBookMarkListener = {
                toggleSelectedCity(it)
                searchCity()
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        presenter = SearchPresenter(this, Injector.getCityRepository(requireContext()))
        savedInstanceState?.getInt(KEY_RECYCLER_POSITION)
            ?.let { pendingRestoreRecyclerViewPosition.value = it }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
        if (context?.networkState == null) {
            context?.defaultSharedPreferences?.lastSearchInput = ""
            showError(R.string.error_network_no_connection)
        } else {
            requestInputFocus(editTextSearch)
        }
        searchCity()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        recyclerSearchCity?.lastCompletelyVisibleItemPosition
            ?.let { outState.putInt(KEY_RECYCLER_POSITION, it) }
    }

    @SuppressLint("ResourceType")
    private fun initView() {
        with(recyclerSearchCity) {
            adapter = cityAdapter
            setupDefaultItemDecoration()
        }
        defaultSharedPreferences.lastSearchInput
            .takeIf { it.isNotBlank() }
            ?.let(::SpannableStringBuilder)
            ?.let { editTextSearch.text = it }

        toggleStartIcon()
    }

    private fun initListener() {
        textInputLayoutSearch.setStartIconOnClickListener {
            if (defaultSharedPreferences.selectedCityId != City.default.id) {
                findNavigator()?.popBackStack()
            } else {
                showError(R.string.error_search_need_select_city)
            }
        }

        editTextSearch.addTextChangedListener { searchCity() }

        textInputLayoutSearch.setEndIconOnClickListener {
            editTextSearch.takeUnless { it.text.isNullOrEmpty() }?.run { text = null }
                ?: searchUsingDeviceLocation()
        }
    }

    private fun searchCity() {
        editTextSearch.text?.toString()?.let { input ->
            defaultSharedPreferences.lastSearchInput = input.trim()
            toggleEndIcon(input)
            imageBackground.setImageResource(R.drawable.ic_round_search_24)
            if (input.isNotBlank()) {
                presenter?.searchCity(input, defaultSharedPreferences.citySearchingLimit)
                textBackground.setText(R.string.title_search_text_background)
            } else if (input.isEmpty()) {
                presenter?.getFetchedCities()
                imageBackground.setImageResource(R.drawable.ic_round_collections_bookmark_24)
                textBackground.setText(R.string.title_search_available_cities)
            }
            if (defaultSharedPreferences.selectedCityId == City.default.id) {
                textBackground.setText(R.string.title_search_text_background_first_time)
            }
        }
    }

    private fun toggleEndIcon(input: String) = input.isEmpty().let { empty ->
        textInputLayoutSearch.endIconDrawable = ContextCompat.getDrawable(
            requireContext(),
            if (empty) R.drawable.ic_round_my_location_24
            else R.drawable.ic_round_cancel_24
        )

        val suffixStringRes =
            if (empty) R.string.title_search_use_device_location else R.string.title_search_clear
        textInputLayoutSearch.suffixText = getString(suffixStringRes)
    }

    private fun toggleStartIcon() {
        if (defaultSharedPreferences.selectedCityId == City.default.id) {
            textInputLayoutSearch.setStartIconDrawable(R.drawable.ic_round_help_outline_24)
        } else {
            textInputLayoutSearch.setStartIconDrawable(R.drawable.ic_round_arrow_back_24)
        }
    }

    private fun requestInputFocus(view: View) {
        view.requestFocus()
        context?.showSoftInput(view)
    }

    private fun toggleSelectedCity(cityId: Int) = with(defaultSharedPreferences) {
        selectedCityId = if (selectedCityId != cityId) cityId else City.default.id
        toggleStartIcon()
    }

    override fun showSearchResult(cities: List<City>) {
        CallbackAsyncTask<List<City>, List<CityItem>>(
            handler = {
                val selectedCity = defaultSharedPreferences.selectedCityId
                it.map { city -> CityItem(city, city.id == selectedCity) }
            },
            onFinishedListener = { result ->
                when (result) {
                    is Result.Success -> cityAdapter.submitList(result.data) {
                        pendingRestoreRecyclerViewPosition.value
                            ?.let { recyclerSearchCity.scrollToPosition(it) }
                    }
                    is Result.Error -> showError(R.string.error_search_result_error)
                    null -> showError(R.string.error_unknown)
                }
            }
        ).executeOnExecutor(cities)
    }

    override fun showError(errorStringRes: Int) {
        context?.showToast(getString(errorStringRes))
    }

    private fun searchUsingDeviceLocation() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
                if (isGranted.all { it.value }) {
                    performSearchUsingDeviceLocation()
                } else {
                    showError(R.string.error_search_location_permission_denied)
                }
            }
        when {
            ifPermissionGranted(permissions) -> performSearchUsingDeviceLocation()
            permissions.any(::shouldShowRequestPermissionRationale) -> {
                val title = getString(R.string.title_search_location_dialog)
                val msg = getString(
                    R.string.format_msg_search_location_dialog_location_require,
                    permissions.joinToString(", ")
                )
                showLocationDialog(title, msg) {
                    setPositiveButton(android.R.string.yes) { _, _ ->
                        requestPermissionLauncher.launch(permissions)
                    }
                }
            }
            else -> requestPermissionLauncher.launch(permissions)
        }
    }

    private fun ifPermissionGranted(permissions: Array<String>) = permissions.all {
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
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        presenter?.searchCityByLocation(location)
                    } else {
                        showError(R.string.error_search_location_null)
                    }
                }
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
    }
}
