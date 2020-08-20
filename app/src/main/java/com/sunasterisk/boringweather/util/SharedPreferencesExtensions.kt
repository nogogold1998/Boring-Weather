package com.sunasterisk.boringweather.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.data.model.City

class DefaultSharedPreferences(
    context: Context
) : SharedPreferences by PreferenceManager.getDefaultSharedPreferences(context) {

    class PreCachedStrings(private val delegate: Map<Int, String>) : Map<Int, String> by delegate {
        override fun get(key: Int): String =
            delegate[key] ?: throw IllegalArgumentException("StringRes $key hasn't been included")
    }

    private val preCachedStrings =
        preferenceStringRes.associateWith(context::getString).let(::PreCachedStrings)

    var isFirstLaunch: Boolean
        set(value) = edit {
            putBoolean(preCachedStrings[R.string.pref_key_is_first_launch], value)
        }
        get() = getBoolean(
            preCachedStrings[R.string.pref_key_is_first_launch],
            preCachedStrings[R.string.pref_value_is_first_launch_default].toBoolean()
        )

    @Deprecated("use Flow")
    var selectedCityId: Int
        set(value) = edit {
            putInt(preCachedStrings[R.string.pref_key_selected_city_id], value)
        }
        get() = getInt(
            preCachedStrings[R.string.pref_key_selected_city_id],
            City.default.id
        )

    val unitSystem: UnitSystem
        get() = when (getString(preCachedStrings[R.string.pref_key_setting_unit_system], null)
            ?: preCachedStrings[R.string.pref_value_setting_unit_system_default]) {
            preCachedStrings[R.string.pref_value_setting_unit_system_imperial] -> UnitSystem.IMPERIAL
            preCachedStrings[R.string.pref_value_setting_unit_system_metric] -> UnitSystem.METRIC
            preCachedStrings[R.string.pref_value_setting_unit_system_international] -> UnitSystem.INTERNATIONAL
            else -> UnitSystem.INTERNATIONAL
        }

    var lastSearchInput: String
        set(value) = edit {
            putString(preCachedStrings[R.string.pref_key_last_searched_city], value)
        }
        get() = getString(preCachedStrings[R.string.pref_key_last_searched_city], null) ?: ""

    var citySearchingLimit: Int
        set(value) = edit {
            putInt(preCachedStrings[R.string.pref_key_setting_search_limit], value)
        }
        get() = getInt(
            preCachedStrings[R.string.pref_key_setting_search_limit],
            preCachedStrings[R.string.pref_value_setting_search_limit_default].toInt()
        )

    companion object {
        private val preferenceStringRes = listOf(
            R.string.pref_key_is_first_launch,
            R.string.pref_value_is_first_launch_default,
            R.string.pref_key_selected_city_id,
            R.string.pref_key_setting_unit_system,
            R.string.pref_value_setting_unit_system_default,
            R.string.pref_value_setting_unit_system_imperial,
            R.string.pref_value_setting_unit_system_international,
            R.string.pref_value_setting_unit_system_metric,
            R.string.pref_key_last_searched_city,
            R.string.pref_key_setting_search_limit,
            R.string.pref_value_setting_search_limit_default
        )

        private var instance: DefaultSharedPreferences? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: DefaultSharedPreferences(context).also { instance = it }
        }
    }
}
