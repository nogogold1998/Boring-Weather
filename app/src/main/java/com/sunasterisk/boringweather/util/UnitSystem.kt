package com.sunasterisk.boringweather.util

import android.content.res.Resources
import com.sunasterisk.boringweather.R

enum class UnitSystem {
    METRIC, IMPERIAL, INTERNATIONAL;

    fun formatTemperature(temperature: Float, resources: Resources) =
        resources.getString(
            when (this) {
                METRIC -> R.string.format_temperature_metric
                IMPERIAL -> R.string.format_temperature_imperial
                INTERNATIONAL -> R.string.format_temperature_international
            },
            temperature
        )

    fun formatDistance(visibility: Int?, resources: Resources) =
        (visibility ?: "--").let {
            resources.getString(
                when (this) {
                    METRIC -> R.string.format_visibility_metric
                    IMPERIAL -> R.string.format_visibility_imperial
                    INTERNATIONAL -> R.string.format_visibility_international
                },
                it
            )
        }

    fun formatSpeed(windSpeed: Float, resources: Resources) =
        resources.getString(
            when (this) {
                METRIC -> R.string.format_speed_metric
                IMPERIAL -> R.string.format_speed_imperial
                INTERNATIONAL -> R.string.format_speed_international
            },
            windSpeed
        )

    fun formatPressure(pressure: Int, resources: Resources) =
        resources.getString(
            R.string.format_pressure,
            pressure.toFloat() / Constants.PRESSURE_EXCHANGE_RATIO
        )
}
