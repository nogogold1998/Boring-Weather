package com.sunasterisk.boringweather.data.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sunasterisk.boringweather.DummyJsonData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test

class DailyWeatherTest {
    private val typeToken = TypeToken.get(DailyWeather::class.java).type
    private val gson = Gson()

    @Test
    fun fromJsonStringToInstance() {
        val given = DummyJsonData.daily1
        // when
        val actual = gson.fromJson<DailyWeather>(given.second, typeToken)
        // then
        assertThat(actual, equalTo(given.first))
    }
}
