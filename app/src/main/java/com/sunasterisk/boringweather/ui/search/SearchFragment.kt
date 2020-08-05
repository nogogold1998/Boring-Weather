package com.sunasterisk.boringweather.ui.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.BaseFragment
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.di.Injector
import com.sunasterisk.boringweather.ui.main.findNavigator
import com.sunasterisk.boringweather.util.showSoftInput
import com.sunasterisk.boringweather.util.showToast
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment(), SearchContract.View {
    override val layoutResource: Int get() = R.layout.fragment_search

    override var presenter: SearchContract.Presenter? = null

    private val cityAdapter = CityAdapter {

        setFragmentResult(
            SearchConstants.KEY_REQUEST_SEARCH_CITY,
            bundleOf(SearchConstants.KEY_BUNDLE_CITY_ID to it)
        )
        findNavigator()?.popBackStack()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = SearchPresenter(this, Injector.getCityRepository(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
        requestInputFocus(editTextSearch)
    }

    private fun initView() {
        recyclerSearchCity.adapter = cityAdapter
    }

    private fun initListener() {
        textInputLayoutSearch.setStartIconOnClickListener {
            findNavigator()?.popBackStack()
        }

        textInputLayoutSearch.setEndIconOnClickListener {
            editTextSearch.text?.toString()?.let { presenter?.searchCity(it) }
        }

        editTextSearch.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    v.text?.toString()?.let { presenter?.searchCity(it) }
                    true
                }
                else -> false
            }
        }
    }

    private fun requestInputFocus(view: View) {
        view.requestFocus()
        context?.showSoftInput(view)
    }

    override fun showSearchResult(cities: List<City>) {
        cityAdapter.submitList(cities)
    }

    override fun showError(errorStringRes: Int) {
        context?.showToast(getString(errorStringRes))
    }
}
