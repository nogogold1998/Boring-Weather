package com.sunasterisk.boringweather.ui.setting

import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.ui.main.findNavigator
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarSettings.setNavigationOnClickListener {
            findNavigator()?.popBackStack()
        }
    }
}
