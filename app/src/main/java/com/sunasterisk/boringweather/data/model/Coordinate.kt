package com.sunasterisk.boringweather.data.model

import android.location.Location
import androidx.room.ColumnInfo
import com.sunasterisk.boringweather.data.source.local.CityTable
import com.sunasterisk.boringweather.util.getOrElse
import org.json.JSONObject

data class Coordinate(
    @ColumnInfo(name = CityTable.COL_COORDINATE_LON)
    val longitude: Float = 0f,
    @ColumnInfo(name = CityTable.COL_COORDINATE_LAT)
    val latitude: Float = 0f
) {

    constructor(location: Location) : this(
        location.longitude.toFloat(),
        location.latitude.toFloat()
    )

    @Deprecated("")
    constructor(jsonObject: JSONObject) : this(
        jsonObject.getOrElse(LON, 0f),
        jsonObject.getOrElse(LAT, 0f)
    )

    companion object {
        const val LON = "lon"
        const val LAT = "lat"
    }
}
