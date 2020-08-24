package com.sunasterisk.boringweather.service

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.core.app.JobIntentService
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.data.source.local.room.AppRoomDatabase
import com.sunasterisk.boringweather.util.Constants
import com.sunasterisk.boringweather.util.buildNotification
import com.sunasterisk.boringweather.util.createNotificationChannel
import com.sunasterisk.boringweather.util.defaultSharedPreferences
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class PrePopulateDatabaseService : JobIntentService() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(
            R.string.notify_prepopulate_channel_id,
            R.string.notify_prepopulate_channel_name,
            R.string.notify_prepopulate_channel_description
        )
        startForeground(
            Constants.NOTIFICATION_ID_PREPOPULATE_DATABASE_SERVICE,
            buildNotification(R.string.notify_prepopulate_channel_id)
        )
    }

    override fun onHandleWork(intent: Intent) {
        intent.takeIf { it.action == Constants.ACTION_PREPOPULATE_DATABASE }
            ?.getStringExtra(Constants.EXTRA_URL_STRING_PREPOPULATE_DATABASE)
            ?.let(::downloadThenPopulateDatabase)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }

    private fun downloadThenPopulateDatabase(urlString: String) {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        try {
            connection.connect()
            val sqlStr = connection.inputStream.bufferedReader().readText()
            AppRoomDatabase.getInstance(applicationContext)
                .openHelper
                .writableDatabase
                .execSQL(sqlStr)
            notifySuccess()
        } catch (e: Exception) {
            when {
                e.message?.matches("""UNIQUE constraint failed: city\.id.*""".toRegex()) == true ->
                    e.printStackTrace()
                e is IOException -> {
                    notifyError(R.string.error_network_lost)
                    e.printStackTrace()
                }
                else -> throw e
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun notifySuccess() = Intent(Constants.ACTION_PREPOPULATE_DATABASE)
        .putExtra(Constants.EXTRA_SUCCEEDED_PREPOPULATE_DATABASE, true)
        .let(::sendBroadcast).also { defaultSharedPreferences.isFirstLaunch = false }

    private fun notifyError(@StringRes msg: Int = R.string.error_unknown) =
        Intent(Constants.ACTION_PREPOPULATE_DATABASE)
            .putExtra(Constants.EXTRA_FAILED_PREPOPULATE_DATABASE, msg)
            .let(::sendBroadcast)

    companion object {
        private const val JOB_ID = 1608

        fun enqueueWork(context: Context, intent: Intent) =
            enqueueWork(context, PrePopulateDatabaseService::class.java, JOB_ID, intent)
    }
}
