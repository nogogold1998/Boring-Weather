package com.sunasterisk.boringweather.ui.binding

import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter
import com.sunasterisk.boringweather.data.model.DailyWeather
import com.sunasterisk.boringweather.data.model.HourlyWeather
import com.sunasterisk.boringweather.util.TimeUtils
import com.sunasterisk.boringweather.util.UnitSystem

@BindingAdapter("unitSystem", "temperature", "emojiEnabled")
fun TextView.bindTemperature(unitSystem: UnitSystem?, temperature: Float?, withEmoji: Boolean?) {
    if (unitSystem != null && temperature != null && withEmoji != null) {
        text = unitSystem.formatTemperature(temperature, resources, withEmoji)
    }
}

@BindingAdapter("dateTime", "dateTimeFormat")
fun TextView.formatTime(dateTime: Long?, dateTimeFormat: String?) {
    if (dateTime != null && dateTimeFormat != null) {
        text = TimeUtils.formatToString(dateTimeFormat, dateTime)
    }
}

@BindingAdapter("distance", "unitSystem")
fun TextView.bindDistance(distance: Int?, unitSystem: UnitSystem?) {
    if (distance != null && unitSystem != null) {
        text = unitSystem.formatDistance(distance, resources)
    }
}

@BindingAdapter("speed", "unitSystem")
fun TextView.bindSpeed(speed: Float?, unitSystem: UnitSystem?) {
    if (speed != null && unitSystem != null) {
        text = unitSystem.formatSpeed(speed, resources)
    }
}

@BindingAdapter("pressure", "unitSystem")
fun TextView.bindPressure(pressure: Int?, unitSystem: UnitSystem?) {
    if (pressure != null && unitSystem != null) {
        text = unitSystem.formatPressure(pressure, resources)
    }
}

// not the best solution but works
@BindingAdapter("uvIndexWeatherEntry", "titleTextViewId")
fun TextView.bindUvIndex(weatherEntry: Any?, @IdRes titleTextViewId: Int?) {
    when (weatherEntry) {
        is HourlyWeather -> text = weatherEntry.uvIndex?.toString()
        is DailyWeather -> text = weatherEntry.uvIndex.toString()
    }
    if (titleTextViewId != null) {
        val visibility = if (text.isNullOrBlank()) View.GONE else View.VISIBLE
        rootView?.findViewById<View>(titleTextViewId)?.let {
            it.visibility = visibility
            this.visibility = visibility
        }
    }
}
