package com.sunasterisk.boringweather.data.local.model

import com.sunasterisk.boringweather.util.getOrElse
import com.sunasterisk.boringweather.util.getOrNull
import org.json.JSONObject

/**
 * @param id - Weather condition id
 * @param main - Group of weather parameters (Rain, Snow, Extreme etc.)
 * @param description - Weather condition within the group.
 * You can get the output in your language. https://openweathermap.org/current#multi
 * @param icon - Weather icon id
 */
data class Weather(
    val id: Int = 0,
    val main: String = "",
    val description: String = "",
    val icon: String? = null
) {

    constructor(jsonObject: JSONObject) : this(
        jsonObject.getOrElse(SERIALIZED_NAME_ID, 0),
        jsonObject.getOrElse(SERIALIZED_NAME_MAIN, ""),
        jsonObject.getOrElse(SERIALIZED_NAME_DESCRIPTION, ""),
        jsonObject.getOrNull(SERIALIZED_NAME_ICON)
    )

    companion object {
        private const val SERIALIZED_NAME_ID = "id"
        private const val SERIALIZED_NAME_MAIN = "main"
        private const val SERIALIZED_NAME_DESCRIPTION = "description"
        private const val SERIALIZED_NAME_ICON = "icon"
    }
}
