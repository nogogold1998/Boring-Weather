package com.sunasterisk.boringweather.service

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import androidx.core.app.JobIntentService
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.data.source.local.AppDatabase
import com.sunasterisk.boringweather.util.Constants
import com.sunasterisk.boringweather.util.buildNotification
import com.sunasterisk.boringweather.util.createNotificationChannel
import java.net.URL

class PrePopulateDatabaseService : JobIntentService() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(
            R.string.notify_prepopulate_channel_id,
            R.string.notify_prepopulate_channel_name,
            R.string.notify_prepopulate_channel_description
        )
    }

    override fun onHandleWork(intent: Intent) {
        startForeground(
            Constants.NOTIFICATION_ID_PREPOPULATE_DATABASE_SERVICE,
            buildNotification(R.string.notify_prepopulate_channel_id)
        )
        intent.getStringExtra(Constants.EXTRA_URL_STRING_PREPOPULATE_DATABASE_SERVICE)?.let {
            downloadThenPopulateDatabase(it)
            stopForeground(true)
            sendBroadcast(Intent().apply {
                action = Constants.ACTION_PREPOPULATE_DATABASE
                putExtra(Constants.EXTRA_URL_STRING_PREPOPULATE_DATABASE_SERVICE, it)
            })
        }
    }

    private fun downloadThenPopulateDatabase(urlString: String) = try {
        val url = URL(urlString)
        val connection = url.openConnection()
        connection.connect()
        val inStream = connection.getInputStream()
        val sql = inStream.bufferedReader().readText()
        AppDatabase.getInstance(this@PrePopulateDatabaseService)
            .writableDatabase.execSQL(sql)
    } catch (e: SQLiteConstraintException) {
        if (e.message?.matches("""UNIQUE constraint failed: city\.id.*""".toRegex()) == true) {
            e.printStackTrace()
        } else throw e
    }

    companion object {
        private const val JOB_ID = 1608

        fun enqueueWork(context: Context, intent: Intent) =
            enqueueWork(context, PrePopulateDatabaseService::class.java, JOB_ID, intent)
    }
}
