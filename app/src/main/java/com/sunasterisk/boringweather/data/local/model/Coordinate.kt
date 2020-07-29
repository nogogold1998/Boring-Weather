package com.sunasterisk.boringweather.data.local.model

import com.sunasterisk.boringweather.util.getOrElse
import org.json.JSONObject

data class Coordinate(val longitude: Float = 0f, val latitude: Float = 0f) {
    constructor(jsonObject: JSONObject) : this(
        jsonObject.getOrElse(LON, 0f),
        jsonObject.getOrElse(LAT, 0f)
    )

    companion object {
        const val LON = "lon"
        const val LAT = "lat"
    }
}
