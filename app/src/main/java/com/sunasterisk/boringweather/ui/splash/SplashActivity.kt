package com.sunasterisk.boringweather.ui.splash

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.Single
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.service.PrePopulateDatabaseService
import com.sunasterisk.boringweather.ui.main.MainActivity
import com.sunasterisk.boringweather.util.Constants
import com.sunasterisk.boringweather.util.connectivityManager
import com.sunasterisk.boringweather.util.defaultSharedPreferences
import com.sunasterisk.boringweather.util.load
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    private var receiver: SplashReceiver? = null

    private val single = Single<() -> Unit>()

    private val defaultNetworkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            super.onLost(network)
            showError()
            single.value = { startPrepopulateDatabaseService() }
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            single.value?.let {
                it.invoke()
                showLoading()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (defaultSharedPreferences.isFirstLaunch) {
            // if (networkState == null) showError() TODO
            single.value = { startPrepopulateDatabaseService() }
        }

        imageSplash.load(getString(R.string.url_plash_weather_icon_gif)) {
            error(R.drawable.ic_round_wifi_off_24)
            fallback(R.drawable.ic_round_wifi_off_24)
        }
    }

    private fun showError(stringRes: Int? = null) = runOnUiThread {
        textStatus.setText(stringRes ?: R.string.error_network_no_connection)
        progressBar.visibility = View.INVISIBLE
    }

    private fun showLoading() = runOnUiThread {
        textStatus.setText(R.string.title_splash_status_setup)
        progressBar.visibility = View.VISIBLE
    }

    private fun startPrepopulateDatabaseService() {
        PrePopulateDatabaseService.enqueueWork(
            this@SplashActivity, Intent(Constants.ACTION_PREPOPULATE_DATABASE).putExtra(
                Constants.EXTRA_URL_STRING_PREPOPULATE_DATABASE,
                getString(R.string.url_prepopulate_city_table_sql)
            )
        )
    }

    override fun onStart() {
        super.onStart()
        if (defaultSharedPreferences.isFirstLaunch) {
            receiver = SplashReceiver()
            registerReceiver(receiver, IntentFilter(Constants.ACTION_PREPOPULATE_DATABASE))
        } else navigateToMainActivity()

        connectivityManager?.registerDefaultNetworkCallback(defaultNetworkCallback)
    }

    override fun onStop() {
        super.onStop()
        receiver?.let(this::unregisterReceiver).also { receiver = null }
        connectivityManager?.unregisterNetworkCallback(defaultNetworkCallback)
    }

    private fun navigateToMainActivity() {
        defaultSharedPreferences.run {
            textStatus.text = getString(
                if (selectedCityId == City.default.id) R.string.title_splash_status_ready
                else R.string.title_splash_status_welcome_back
            )
            isFirstLaunch = false
        }
        textStatus.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, START_ACTIVITY_DELAY_MILLIS)
    }

    inner class SplashReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getBooleanExtra(Constants.EXTRA_SUCCEEDED_PREPOPULATE_DATABASE, false)
                ?.takeIf { it }?.let { navigateToMainActivity() }
                ?: intent?.getIntExtra(Constants.EXTRA_FAILED_PREPOPULATE_DATABASE, -1)
                    .takeIf { it != -1 }?.let { showError() }
        }
    }

    companion object {
        private const val START_ACTIVITY_DELAY_MILLIS = 2500L
    }
}
