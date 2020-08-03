package com.sunasterisk.boringweather.data.model

import android.content.ContentValues
import android.content.res.Resources
import android.database.Cursor
import androidx.annotation.RawRes
import androidx.annotation.WorkerThread
import com.sunasterisk.boringweather.data.source.local.CityTable
import com.sunasterisk.boringweather.util.getOrElse
import com.sunasterisk.boringweather.util.map
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader

data class City(
    val id: Int,
    val name: String,
    val country: String,
    val coordinate: Coordinate
) {
    constructor(jsonObject: JSONObject) : this(
        jsonObject.getInt(ID),
        jsonObject.getString(NAME),
        jsonObject.getString(COUNTRY),
        jsonObject.getJSONObject(COORDINATE).let(::Coordinate)
    )

    constructor(cursor: Cursor) : this(
        cursor.getOrElse(CityTable.COL_ID, default.id),
        cursor.getOrElse(CityTable.COL_NAME, default.name),
        cursor.getOrElse(CityTable.COL_COUNTRY, default.country),
        Coordinate(
            cursor.getOrElse(CityTable.COL_COORDINATE_LAT, default.coordinate.latitude),
            cursor.getOrElse(CityTable.COL_COORDINATE_LON, default.coordinate.longitude)
        )
    )

    fun getContentValues() = ContentValues().apply {
        put(CityTable.COL_ID, id)
        put(CityTable.COL_NAME, name)
        put(CityTable.COL_COUNTRY, country)
        put(CityTable.COL_COORDINATE_LAT, coordinate.latitude)
        put(CityTable.COL_COORDINATE_LON, coordinate.longitude)
    }

    companion object {
        val default = City(0, "", "", Coordinate())

        @WorkerThread
        fun getFromRaw(resource: Resources, @RawRes rawId: Int): List<City> {
            val inputStream = resource.openRawResource(rawId)
            val jsonString = inputStream.bufferedReader().use(BufferedReader::readText)
            return JSONArray(jsonString).map(::City)
        }

        private const val ID = "id"
        private const val NAME = "name"
        private const val COUNTRY = "country"
        private const val COORDINATE = "coord"
    }
}
