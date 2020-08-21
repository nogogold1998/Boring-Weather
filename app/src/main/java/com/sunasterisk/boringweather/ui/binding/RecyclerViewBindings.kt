package com.sunasterisk.boringweather.ui.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sunasterisk.boringweather.ui.search.CityAdapter
import com.sunasterisk.boringweather.ui.search.model.CityItem

@BindingAdapter("cityList")
fun RecyclerView.submitCityItems(cityItems: List<CityItem>?) {
    (adapter as? CityAdapter)?.submitList(cityItems)
}
