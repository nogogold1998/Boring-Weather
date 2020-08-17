package com.sunasterisk.boringweather.data.model

import com.google.gson.annotations.SerializedName
import com.sunasterisk.boringweather.util.getOrElse
import org.json.JSONObject

data class Temperature(
    val day: Float,
    val min: Float,
    val max: Float,
    val night: Float,
    @SerializedName(EVENING) val evening: Float,
    @SerializedName(MORNING) val morning: Float
) {
    val average: Float
        get() = listOf(day, min, max, night, evening, morning)
            .filter { it != 0f }
            .average()
            .toFloat()

    constructor(jsonObject: JSONObject) : this(
        jsonObject.getOrElse(DAY, default.day),
        jsonObject.getOrElse(MIN, default.min),
        jsonObject.getOrElse(MAX, default.max),
        jsonObject.getOrElse(NIGHT, default.night),
        jsonObject.getOrElse(EVENING, default.evening),
        jsonObject.getOrElse(MORNING, default.morning)
    )

    companion object {
        val default = Temperature(0f, 0f, 0f, 0f, 0f, 0f)

        private const val DAY = "day"
        private const val MIN = "min"
        private const val MAX = "max"
        private const val NIGHT = "night"
        private const val EVENING = "eve"
        private const val MORNING = "morn"
    }
}
