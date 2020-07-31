package com.sunasterisk.boringweather.data.model

import com.sunasterisk.boringweather.util.getOrElse
import org.json.JSONObject

data class Temperature(
    val day: Float = 0f,
    val min: Float = 0f,
    val max: Float = 0f,
    val night: Float = 0f,
    val evening: Float = 0f,
    val morning: Float = 0f
) {

    constructor(jsonObject: JSONObject): this(
        jsonObject.getOrElse(DAY, 0f),
        jsonObject.getOrElse(MIN, 0f),
        jsonObject.getOrElse(MAX, 0f),
        jsonObject.getOrElse(NIGHT, 0f),
        jsonObject.getOrElse(EVENING, 0f),
        jsonObject.getOrElse(MORNING, 0f)
    )

    companion object {

        private const val DAY = "day"
        private const val MIN = "min"
        private const val MAX = "max"
        private const val NIGHT = "night"
        private const val EVENING = "evening"
        private const val MORNING = "morning"
    }
}
