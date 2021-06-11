package com.sunasterisk.boringweather.ui.search

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.sunasterisk.boringweather.DummyJsonData
import com.sunasterisk.boringweather.data.source.local.room.CityDataSource
import com.sunasterisk.boringweather.getOrAwaitValue
import com.sunasterisk.boringweather.util.DefaultSharedPreferences
import com.sunasterisk.boringweather.util.defaultSharedPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
@MediumTest
class SearchViewModelTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val taskExecutor = InstantTaskExecutorRule()

    // subject
    private lateinit var searchViewModel: SearchViewModel

    @Mock
    lateinit var cityRepository: CityDataSource

    private lateinit var defaultSharedPreferences: DefaultSharedPreferences

    private val hanoi = DummyJsonData.hanoi
    private val haiphong = DummyJsonData.haiphong

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().context
        defaultSharedPreferences = context.defaultSharedPreferences
        defaultSharedPreferences.lastSearchInput = hanoi.name
        searchViewModel = SearchViewModel(
            cityRepository,
            defaultSharedPreferences
        )
    }

    @Test
    fun getSearchInput() {
        run {
            val actualSearchInput = searchViewModel.searchInput.getOrAwaitValue()
            assertThat(actualSearchInput, equalTo(hanoi.name))
        }
        run {
            val newInput = haiphong.name
            searchViewModel.searchInput.value = newInput
            val actualNewInput = defaultSharedPreferences.lastSearchInput
            assertThat(actualNewInput, equalTo(newInput))
        }
    }
}
