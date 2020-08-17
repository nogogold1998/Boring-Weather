package com.sunasterisk.boringweather.data.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sunasterisk.boringweather.DummyJsonData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test
import java.lang.reflect.Type

class WeatherTest {
    private val typeToken: Type = object : TypeToken<Weather>() {}.type
    private val gson = Gson()

    @Test
    fun fromJsonStringToInstance() {
        val given = listOf(DummyJsonData.weather1, DummyJsonData.weather2)

        given.forEach {
            // when
            val actual = gson.fromJson<Weather>(it.second, typeToken)
            // then
            assertThat(actual, `is`(it.first))
        }
    }
}
