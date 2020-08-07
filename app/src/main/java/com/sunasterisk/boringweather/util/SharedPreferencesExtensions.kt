package com.sunasterisk.boringweather.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.data.model.City

class DefaultSharedPreferences private constructor(
    sharedPreferences: SharedPreferences,
    private val context: Context
) : SharedPreferences by sharedPreferences {

    var isFirstLaunch: Boolean
        set(value) = edit()
            .putBoolean(context.getString(R.string.pref_key_is_first_launch), value)
            .apply()
        get() = getBoolean(
            context.getString(R.string.pref_key_is_first_launch),
            context.getString(R.string.pref_value_is_first_launch_default).toBoolean()
        )

    var selectedCityId: Int
        set(value) = edit { putInt(context.getString(R.string.pref_key_selected_city_id), value) }
        get() = getInt(context.getString(R.string.pref_key_selected_city_id), City.default.id)

    val unitSystem: UnitSystem
        get() = with(context) {
            return when (this@DefaultSharedPreferences.getString(
                getString(R.string.pref_key_setting_unit_system), null
            ) ?: getString(R.string.pref_value_setting_unit_system_default)) {
                getString(R.string.pref_value_setting_unit_system_imperial) -> UnitSystem.IMPERIAL
                getString(R.string.pref_value_setting_unit_system_metric) -> UnitSystem.METRIC
                getString(R.string.pref_value_setting_unit_system_international) -> UnitSystem.INTERNATIONAL
                else -> UnitSystem.INTERNATIONAL
            }
        }

    var lastSearchedCity: String
        set(value) = edit {
            putString(context.getString(R.string.pref_key_last_searched_city), value)
        }
        get() = getString(context.getString(R.string.pref_key_last_searched_city), null) ?: ""

    companion object {
        private var instance: DefaultSharedPreferences? = null
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: DefaultSharedPreferences(PreferenceManager.getDefaultSharedPreferences(context), context)
                .also { instance = it }
        }
    }
}
