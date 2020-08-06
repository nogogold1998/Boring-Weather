package com.sunasterisk.boringweather.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.sunasterisk.boringweather.R

fun Context.showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}

fun Context.showSoftInput(view: View, flag: Int = InputMethodManager.SHOW_IMPLICIT) =
    ContextCompat.getSystemService(this, InputMethodManager::class.java)
        ?.showSoftInput(view, flag)

fun Context.createNotificationChannel(
    @StringRes channelId: Int,
    @StringRes channelName: Int,
    @StringRes channelDescription: Int
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        ContextCompat.getSystemService(this, NotificationManager::class.java)?.let {
            if (it.getNotificationChannel(getString(channelId)) == null) {
                val channel = NotificationChannel(
                    getString(channelId),
                    getString(channelName),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = getString(channelDescription) }
                it.createNotificationChannel(channel)
            }
        }
    }
}

fun Context.buildNotification(@StringRes channelId: Int): Notification =
    NotificationCompat.Builder(this, getString(channelId)).apply {
        setContentTitle(getString(R.string.notify_prepopulate_title))
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setSmallIcon(R.drawable.ic_round_cloud_download_24)
        color = ContextCompat.getColor(this@buildNotification, R.color.color_gull_gray)

    }.build()

val Context.defaultSharedPreferences: DefaultSharedPreferences
    get() = DefaultSharedPreferences.getInstance(this)

