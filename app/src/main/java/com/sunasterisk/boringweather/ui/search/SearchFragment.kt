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
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.sunasterisk.boringweather.R
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

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private val searchViewModel: SearchViewModel by viewModels<SearchViewModel> {
        val context = requireContext()
        SearchViewModel.Factory(
            NewInjector.provideCityRepository(context),
            NewInjector.provideLocationLiveData(context),
            context.defaultSharedPreferences
        )
    }

    private val defaultSharedPreferences by lazy { requireContext().defaultSharedPreferences }

    private val pendingRestoreRecyclerViewPosition = Single<Int>()

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

        savedInstanceState?.getInt(KEY_RECYCLER_POSITION)
            ?.let { pendingRestoreRecyclerViewPosition.value = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.searchViewModel = searchViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        searchCity()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        recyclerSearchCity?.lastCompletelyVisibleItemPosition
            ?.let { outState.putInt(KEY_RECYCLER_POSITION, it) }
    }

    @SuppressLint("ResourceType")
    private fun initView() {
        with(binding.recyclerSearchCity) {
            adapter = cityAdapter
            setupDefaultItemDecoration()
        }
        // TODO wrong logic
        // defaultSharedPreferences.lastSearchInput
        //     .takeIf { it.isNotBlank() }
        //     ?.let(::SpannableStringBuilder)
        //     ?.let { editTextSearch.text = it }

        toggleStartIcon()
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

    // TODO observe search input
    private fun searchCity() {
        editTextSearch.text?.toString()?.let { input ->
            defaultSharedPreferences.lastSearchInput = input.trim()
            toggleEndIcon(input)
            imageBackground.setImageResource(R.drawable.ic_round_search_24)
            if (input.isNotBlank()) {
                textBackground.setText(R.string.title_search_text_background)
            } else if (input.isEmpty()) {
                imageBackground.setImageResource(R.drawable.ic_round_collections_bookmark_24)
                textBackground.setText(R.string.title_search_available_cities)
            }
            if (defaultSharedPreferences.selectedCityId == City.default.id) {
                textBackground.setText(R.string.title_search_text_background_first_time)
            }
        }
    }

    // TODO observe search input
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

    // TODO observe search input
    private fun toggleStartIcon() {
        if (defaultSharedPreferences.selectedCityId == City.default.id) {
            textInputLayoutSearch.setStartIconDrawable(R.drawable.ic_round_help_outline_24)
        } else {
            textInputLayoutSearch.setStartIconDrawable(R.drawable.ic_round_arrow_back_24)
        }
    }

    // TODO add in onViewCreated
    private fun requestInputFocus(view: View) {
        view.requestFocus()
        context?.showSoftInput(view)
    }

    @Deprecated("wrong logic")
    private fun toggleSelectedCity(cityId: Int) = with(defaultSharedPreferences) {
        selectedCityId = if (selectedCityId != cityId) cityId else City.default.id
        toggleStartIcon()
    }

    fun showError(errorStringRes: Int) {
        context?.showToast(getString(errorStringRes))
    }

    @Deprecated("wrong logic")
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
                TODO("observe LocationLiveData")
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
