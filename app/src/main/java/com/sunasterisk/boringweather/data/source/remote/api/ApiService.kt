package com.sunasterisk.boringweather.data.source.remote.api

import android.net.Uri
import com.sunasterisk.boringweather.BuildConfig
import com.sunasterisk.boringweather.data.model.Coordinate
import com.sunasterisk.boringweather.data.model.OneCallEntry
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object ApiService {
    fun queryOneCallApi(coordinate: Coordinate): OneCallEntry {
        val uri = Uri.Builder().scheme(ApiConstants.SCHEME_HTTPS)
            .authority(ApiConstants.AUTHORITY_OPENWEATHERMAP_API)
            .appendPath(ApiConstants.PATH_DATA)
            .appendPath(ApiConstants.PATH_2_5)
            .appendPath(ApiConstants.PATH_ONE_CALL)
            .appendQueryParameter(ApiConstants.QUERY_LATITUDE, coordinate.latitude.toString())
            .appendQueryParameter(ApiConstants.QUERY_LONGITUDE, coordinate.longitude.toString())
            .appendQueryParameter(ApiConstants.QUERY_EXCLUDE, ApiConstants.PARAM_EXCLUDE_MINUTELY)
            .appendQueryParameter(ApiConstants.QUERY_API_KEY, BuildConfig.API_KEY)
            .toString()

        val responseString = makeNetworkCall(URL(uri))

        return OneCallEntry(JSONObject(responseString))
    }

    private fun makeNetworkCall(
        url: URL,
        method: String = ApiConstants.METHOD_GET
    ): String {
        var urlConnection: HttpURLConnection? = null
        return try {
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.apply {
                doInput = true
                requestMethod = method
                connect()
            }
            val responseCode = urlConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                url.openStream().bufferedReader().use(BufferedReader::readText)
            } else throw IOException(urlConnection.responseMessage)
        } finally {
            urlConnection?.disconnect()
        }
    }
}
