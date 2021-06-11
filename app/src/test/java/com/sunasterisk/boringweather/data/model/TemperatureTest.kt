package com.sunasterisk.boringweather.data.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sunasterisk.boringweather.DummyJsonData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test
import java.lang.reflect.Type

class TemperatureTest {
    private val typeToken: Type = object : TypeToken<Temperature>() {}.type
    private val gson = Gson()

    @Test
    fun fromJsonStringToInstance() {
        val given = DummyJsonData.temperature1
        // when
        val actual = gson.fromJson<Temperature>(given.second, typeToken)
        // then
        assertThat(actual, `is`(given.first))
    }
}
