package com.sunasterisk.boringweather.data.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sunasterisk.boringweather.DummyJsonData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test

class OneCallEntryTest {
    private val typeToken = TypeToken.get(OneCallEntry::class.java).type
    private val gson = Gson()

    @Test
    fun fromJsonStringToInstance() {
        val given = DummyJsonData.oneCallEntry
        // when
        val actual = gson.fromJson<OneCallEntry>(given.second, typeToken)
        // then
        assertThat(actual.hourly.size, `is`(48))
        assertThat(actual.daily.size, `is`(8))
        assertThat(actual.copy(hourly = emptyList(), daily = emptyList()), `is`(given.first))
    }
}
