package com.sunasterisk.boringweather.data.model

import android.content.ContentValues
import android.database.Cursor
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sunasterisk.boringweather.data.source.local.CityTable
import com.sunasterisk.boringweather.util.getOrElse
import org.json.JSONObject

@Entity
data class City(
    @PrimaryKey val id: Int,
    val name: String,
    val country: String,
    @ColumnInfo(defaultValue = "0") val lastFetch: Long,
    @Embedded val coordinate: Coordinate
) {
    constructor(jsonObject: JSONObject) : this(
        jsonObject.getInt(ID),
        jsonObject.getString(NAME),
        jsonObject.getString(COUNTRY),
        jsonObject.getOrElse(LAST_FETCH, default.lastFetch),
        jsonObject.getJSONObject(COORDINATE).let(::Coordinate)
    )

    constructor(cursor: Cursor) : this(
        cursor.getOrElse(CityTable.COL_ID, default.id),
        cursor.getOrElse(CityTable.COL_NAME, default.name),
        cursor.getOrElse(CityTable.COL_COUNTRY, default.country),
        cursor.getOrElse(CityTable.COL_LAST_FETCH, default.lastFetch),
        Coordinate(
            cursor.getOrElse(CityTable.COL_COORDINATE_LAT, default.coordinate.latitude),
            cursor.getOrElse(CityTable.COL_COORDINATE_LON, default.coordinate.longitude)
        )
    )

    fun getContentValues() = ContentValues().apply {
        put(CityTable.COL_ID, id)
        put(CityTable.COL_NAME, name)
        put(CityTable.COL_COUNTRY, country)
        put(CityTable.COL_LAST_FETCH, lastFetch)
        put(CityTable.COL_COORDINATE_LAT, coordinate.latitude)
        put(CityTable.COL_COORDINATE_LON, coordinate.longitude)
    }

    companion object {
        val default = City(0, "", "", CityTable.DEFAULT_COL_LAST_FETCH, Coordinate())

        private const val ID = "id"
        private const val NAME = "name"
        private const val COUNTRY = "country"
        private const val COORDINATE = "coord"
        private const val LAST_FETCH = "last_fetch"
    }
}
