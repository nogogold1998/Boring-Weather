package com.sunasterisk.boringweather.data.model

import android.content.res.Resources
import androidx.annotation.RawRes
import androidx.annotation.WorkerThread
import com.sunasterisk.boringweather.util.map
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader

data class City(
    val id: Int = 0,
    val name: String = "",
    val country: String = "",
    val coordinate: Coordinate = Coordinate()
) {
    constructor(jsonObject: JSONObject) : this(
        jsonObject.getInt(ID),
        jsonObject.getString(NAME),
        jsonObject.getString(COUNTRY),
        jsonObject.getJSONObject(COORDINATE).let(::Coordinate)
    )

    companion object {

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
