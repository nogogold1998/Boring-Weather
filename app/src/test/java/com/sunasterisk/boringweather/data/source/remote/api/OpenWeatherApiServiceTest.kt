package com.sunasterisk.boringweather.data.source.remote.api

import com.sunasterisk.boringweather.DummyJsonData
import com.sunasterisk.boringweather.TestHelper
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.HttpURLConnection

class OpenWeatherApiServiceTest {
    private lateinit var mockWebServer: MockWebServer

    private lateinit var apiService: OpenWeatherApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiService = OpenWeatherApiService.create(mockWebServer.url("/").toString())
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun fetch() = runBlocking {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(
                TestHelper.readContentFromFilePath(
                    TestHelper.ONE_CALL_JSON_FILE_PATH
                )
            )

        mockWebServer.enqueue(response)

        val coordinate = DummyJsonData.hanoi.coordinate
        val exclude = arrayOf("minutely, hourly")
        val oneCallEntry = apiService.fetchOneCallEntry(coordinate, *exclude)

        assertThat(oneCallEntry.hourly.size, equalTo(48))
        assertThat(oneCallEntry.daily.size, equalTo(8))
        assertThat(oneCallEntry.current.dateTime, equalTo(1597592220L))
    }
}

