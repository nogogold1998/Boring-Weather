package com.sunasterisk.boringweather.util

object Constants {
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

    const val HOUR_TO_MINUTES = 60

    const val DAY_TO_HOURS = 24

    const val DAY_TO_SECONDS = DAY_TO_HOURS * HOUR_TO_MINUTES * MINUTE_TO_SECONDS

    const val NOTIFICATION_ID_PREPOPULATE_DATABASE_SERVICE = 16

    const val EXTRA_URL_STRING_PREPOPULATE_DATABASE_SERVICE = "url_string"

    const val ACTION_PREPOPULATE_DATABASE = "com.sunasterisk.boringweather.PREPOPULATE_DATABASE"
}
