package com.sunasterisk.boringweather.ui.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sunasterisk.boringweather.base.Event
import com.sunasterisk.boringweather.data.model.SummaryWeather
import com.sunasterisk.boringweather.ui.current.SummaryWeatherAdapter
import com.sunasterisk.boringweather.ui.detail.DetailWeatherAdapter
import com.sunasterisk.boringweather.ui.detail.model.DetailWeatherAdapterItem
import com.sunasterisk.boringweather.ui.search.CityAdapter
import com.sunasterisk.boringweather.ui.search.model.CityItem
import com.sunasterisk.boringweather.util.scrollToPositionScrollChangeListenerAware

@BindingAdapter("cityList")
fun RecyclerView.submitCityItems(cityItems: List<CityItem>?) {
    (adapter as? CityAdapter)?.submitList(cityItems)
}

@BindingAdapter("summaryWeatherList")
fun RecyclerView.submitSummaryWeathers(summaryWeather: List<SummaryWeather>?) {
    (adapter as? SummaryWeatherAdapter)?.submitList(summaryWeather)
}

@BindingAdapter("detailWeathersList", "focusItemIndexEvent")
fun RecyclerView.submitDetailsWeatherItems(
    detailWeatherItems: List<DetailWeatherAdapterItem<*>>?,
    focusItemIndexEvent: Event<Int>?,
) {
    (adapter as? DetailWeatherAdapter)?.submitList(detailWeatherItems) {
        focusItemIndexEvent?.getContentIfNotHandled()
            ?.let(this::scrollToPositionScrollChangeListenerAware)
    }
}
