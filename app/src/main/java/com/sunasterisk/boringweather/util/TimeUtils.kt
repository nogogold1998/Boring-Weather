package com.sunasterisk.boringweather.util

import android.text.format.DateFormat
import java.util.Calendar

object TimeUtils {
    fun getCurrentToSeconds() = Calendar.getInstance().timeInMillis / Constants.SECOND_TO_MILLIS

    fun currentToStartOfDay(current: Long) = Calendar.getInstance().apply {
        timeInMillis = current * Constants.SECOND_TO_MILLIS
        set(Calendar.HOUR, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }.timeInMillis / Constants.SECOND_TO_MILLIS

    fun formatToString(inFormat: String, unixTimeStamp: Long) = DateFormat
        .format(inFormat, unixTimeStamp * Constants.SECOND_TO_MILLIS)
        .toString()

    fun currentToStartEndOfDay(current: Long) = Calendar.getInstance().run {
        timeInMillis = current * Constants.SECOND_TO_MILLIS
        set(Calendar.HOUR, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        val startOfDay = timeInMillis / Constants.SECOND_TO_MILLIS
        set(Calendar.HOUR, Constants.DAY_TO_HOURS)
        val endOfDay = timeInMillis / Constants.SECOND_TO_MILLIS
        startOfDay to endOfDay
    }

    const val FORMAT_DATE_LONG_TIME_SHORT_ = "MMM dd yyyy, h:mm a"

    const val FORMAT_TIME_SHORT = "h:mm a"

    const val FORMAT_DATE_SHORT = "MMM dd"
}
