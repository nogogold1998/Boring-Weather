package com.sunasterisk.boringweather.data.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sunasterisk.boringweather.DummyJsonData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test

class HourlyWeatherTest {
    private val typeToken = TypeToken.get(HourlyWeather::class.java).type
    private val gson = Gson()

    @Test
    fun fromJsonStringToInstance() {
        val given = listOf(DummyJsonData.current, DummyJsonData.hourly1)

        given.forEach {
            // when
            val actual = gson.fromJson<HourlyWeather>(it.second, typeToken)
            // then
            assertThat(actual, equalTo(it.first))
        }
    }
}
