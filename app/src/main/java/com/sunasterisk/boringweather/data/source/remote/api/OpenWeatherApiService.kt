package com.sunasterisk.boringweather.data.source.remote.api

import androidx.annotation.VisibleForTesting
import com.sunasterisk.boringweather.BuildConfig
import com.sunasterisk.boringweather.data.model.Coordinate
import com.sunasterisk.boringweather.data.model.OneCallEntry
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApiService {

    @GET("${ApiConstants.PATH_DATA}/${ApiConstants.PATH_2_5}/${ApiConstants.PATH_ONE_CALL}")
    suspend fun fetchOneCallEntry(
        @Query(ApiConstants.QUERY_LATITUDE) latitude: Float,
        @Query(ApiConstants.QUERY_LONGITUDE) longitude: Float,
        @Query(ApiConstants.QUERY_EXCLUDE) exclude: String
    ): OneCallEntry

    companion object {

        private const val baseUrl =
            "${ApiConstants.SCHEME_HTTPS}://${ApiConstants.AUTHORITY_OPENWEATHERMAP_API}/"

        fun create() = create(baseUrl)

        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        fun create(url: String): OpenWeatherApiService = Retrofit.Builder()
            .baseUrl(url)
            .client(createOkHttpClientBuilder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherApiService::class.java)

        private fun createOkHttpClientBuilder() = OkHttpClient.Builder()
            .addInterceptor { chain ->
                // add api key for each request
                val request = chain.request().newBuilder()
                val originalHttpUrl = chain.request().url
                val url = originalHttpUrl.newBuilder()
                    .addQueryParameter(ApiConstants.QUERY_API_KEY, BuildConfig.API_KEY)
                    .build()
                request.url(url)
                return@addInterceptor chain.proceed(request.build())
            }
            .addInterceptor(HttpLoggingInterceptor().apply { level = Level.BASIC })
    }
}

suspend fun OpenWeatherApiService.fetchOneCallEntry(
    coordinate: Coordinate,
    vararg exclude: String = arrayOf(ApiConstants.PARAM_EXCLUDE_MINUTELY)
) =
    fetchOneCallEntry(coordinate.latitude, coordinate.longitude, exclude.joinToString())
