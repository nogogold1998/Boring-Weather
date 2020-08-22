package com.sunasterisk.boringweather.util

import android.text.format.DateFormat
import java.util.Calendar

object TimeUtils {
    fun getCurrentInSeconds() = System.currentTimeMillis() / Constants.SECOND_TO_MILLIS

    @JvmStatic
    fun formatToString(inFormat: String, unixTimeStamp: Long) = DateFormat
        .format(inFormat, unixTimeStamp * Constants.SECOND_TO_MILLIS)
        .toString()

    fun getStartEndOfDay(current: Long) = Calendar.getInstance().run {
        timeInMillis = current * Constants.SECOND_TO_MILLIS
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        val startOfDay = timeInMillis / Constants.SECOND_TO_MILLIS
        set(Calendar.HOUR_OF_DAY, Constants.DAY_TO_HOURS)
        val endOfDay = timeInMillis / Constants.SECOND_TO_MILLIS
        startOfDay to endOfDay
    }

    const val FORMAT_DATE_LONG_TIME_SHORT_ = "MMM, dd yyyy, h:mm a"

    const val FORMAT_TIME_SHORT = "h:mm a"

    const val FORMAT_DATE_SHORT = "MMM dd"

    const val FORMAT_DATE = "MMM, dd yyyy"
}
