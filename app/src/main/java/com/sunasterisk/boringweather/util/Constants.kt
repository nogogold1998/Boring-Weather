package com.sunasterisk.boringweather.util

object Constants {
    const val SEARCH_LIMIT_DEFAULT = 500

    const val METER_TO_KILOMETER = 1000F
    const val KILOPASCAL_TO_HECTOPASCAL = 10F
    const val KILOMETER_TO_MILES = 0.621371192F
    const val METER_PER_SECOND_TO_MILES_PER_HOUR = 2.23693629F
    const val MULTIPLE_KELVIN_TO_CELSIUS = 1F
    const val OFFSET_KELVIN_TO_CELSIUS = -273.15F
    const val MULTIPLE_KELVIN_TO_FAHRENHEIT = 1.8F
    const val OFFSET_KELVIN_TO_FAHRENHEIT = -459.67F

    const val SECOND_TO_MILLIS = 1000

    const val MINUTE_TO_SECONDS = 60

    const val MINUTE_TO_MILLIS = (MINUTE_TO_SECONDS * SECOND_TO_MILLIS).toLong()

    const val REQUEST_TIMED_OUT = MINUTE_TO_MILLIS / 2

    const val HOUR_TO_MINUTES = 60

    const val HOUR_TO_SECONDS = HOUR_TO_MINUTES * MINUTE_TO_SECONDS

    const val DAY_TO_HOURS = 24

    const val NOTIFICATION_ID_PREPOPULATE_DATABASE_SERVICE = 16

    const val EXTRA_URL_STRING_PREPOPULATE_DATABASE = "url_string_prepopulate_database"
    const val EXTRA_SUCCEEDED_PREPOPULATE_DATABASE = "succeeded_prepopulate_database"
    const val EXTRA_FAILED_PREPOPULATE_DATABASE = "failed_prepopulate_database"

    const val ACTION_PREPOPULATE_DATABASE = "com.sunasterisk.boringweather.PREPOPULATE_DATABASE"
}
