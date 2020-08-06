package com.sunasterisk.boringweather.ui.splash

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.service.PrePopulateDatabaseService
import com.sunasterisk.boringweather.ui.main.MainActivity
import com.sunasterisk.boringweather.util.Constants
import com.sunasterisk.boringweather.util.defaultSharedPreferences

class SplashActivity : AppCompatActivity() {
    private var receiver: SplashReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (defaultSharedPreferences.isFirstLaunch) {
            PrePopulateDatabaseService.enqueueWork(
                this, Intent().putExtra(
                    Constants.EXTRA_URL_STRING_PREPOPULATE_DATABASE_SERVICE, getString(
                        R.string.url_prepopulate_city_table_sql
                    )
                )
            )
        } else {
            navigateToMainActivity()
        }
    }

    override fun onStart() {
        super.onStart()
        if (defaultSharedPreferences.isFirstLaunch) {
            receiver = SplashReceiver()
            registerReceiver(
                receiver,
                IntentFilter().apply { addAction(Constants.ACTION_PREPOPULATE_DATABASE) }
            )
        }
    }

    override fun onStop() {
        super.onStop()
        receiver?.let(this::unregisterReceiver)
    }

    private fun navigateToMainActivity() {
        defaultSharedPreferences.isFirstLaunch = false
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    inner class SplashReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                navigateToMainActivity()
            }
        }
    }
}
