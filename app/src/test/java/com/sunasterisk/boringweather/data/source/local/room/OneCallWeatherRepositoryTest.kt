package com.sunasterisk.boringweather.data.source.local.room

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.sunasterisk.boringweather.DummyJsonData
import com.sunasterisk.boringweather.util.TimeUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
@MediumTest
class OneCallWeatherRepositoryTest {

    @get:Rule
    val instantTaskExecutor = InstantTaskExecutorRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private val hanoi = DummyJsonData.hanoi

    private val testDispatcher = TestCoroutineDispatcher()

    private val testScope = TestCoroutineScope(testDispatcher)

    @Mock
    lateinit var remote: OneCallWeatherDataSource.Remote

    lateinit var database: AppRoomDatabase

    lateinit var cityDataSource: CityDataSource.Local

    lateinit var local: OneCallWeatherDataSource.Local

    // Subject
    lateinit var repo: OneCallWeatherDataSource

    @Before
    fun setUp() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().context.applicationContext
        database = Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
            .allowMainThreadQueries()
            .setQueryExecutor(testDispatcher.asExecutor())
            .setTransactionExecutor(testDispatcher.asExecutor())
            .build()
        val cityDao = database.cityDao()
        local = LocalOneCalWeatherDataSource(
            cityDao,
            database.hourlyWeatherDao(),
            database.dailyWeatherDao(),
            testDispatcher
        )
        cityDao.insertCity(hanoi.copy(lastFetch = 0))
        cityDataSource = LocalCityDataSource(cityDao, testDispatcher)
        repo = OneCallWeatherRepository(
            remote,
            local,
            cityDataSource,
            testDispatcher
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun fetchWeatherData() = testScope.runBlockingTest {
        val oneCallEntries = DummyJsonData.oneCallEntries
        oneCallEntries.forEach { entry ->
            `when`(remote.fetchOneCallWeatherByCoordinate(hanoi.coordinate)).thenReturn(entry)

            repo.fetchWeatherData(hanoi)

            // check
            val actualHanoi = cityDataSource.getCityById(hanoi.id)
            val fetchedCities = cityDataSource.getFetchedCities().first()
            assert(actualHanoi != null)
            assertThat(fetchedCities, hasItems(actualHanoi))
            database.hourlyWeatherDao().let { hourlyDao ->
                val actualHourlyEntities = hourlyDao.findHourlyWeatherEntity(hanoi.id).first()
                assertThat(actualHourlyEntities.size, equalTo((entry.hourly + entry.current).size))
            }
            database.dailyWeatherDao().let { dailyDao ->
                val actualDailyEntities = dailyDao.findDailyWeatherEntity(hanoi.id).first()
                assertThat(actualDailyEntities.size, equalTo(entry.daily.size))
            }
            // clear
            database.hourlyWeatherDao().deleteAllHourWeather()
            database.dailyWeatherDao().deleteAllDailyWeather()
        }
    }

    @Test
    fun getCurrentWeather() = testScope.runBlockingTest {
        val oneCallEntries = DummyJsonData.oneCallEntries
        oneCallEntries.forEach { entry ->
            `when`(remote.fetchOneCallWeatherByCoordinate(hanoi.coordinate)).thenReturn(entry)

            repo.fetchWeatherData(hanoi)

            // check
            val actualHanoi = cityDataSource.getCityById(hanoi.id)
                ?: throw IllegalStateException("city must not be null")

            val actualCurrentWeather =
                repo.getCurrentWeather(hanoi.id, actualHanoi.lastFetch).first()
            assertThat(actualCurrentWeather.city, `is`(actualHanoi))
            assertThat(actualCurrentWeather.currentWeather, `is`(entry.current))
            assertThat(actualCurrentWeather.dailyWeather, isIn(entry.daily))
            assertThat(actualCurrentWeather.currentWeather, equalTo(entry.current))
            assertThat(actualCurrentWeather.forecastSummaryWeathers.size, equalTo(entry.daily.size))
        }
    }

    @Test
    fun getDetailWeather() = testScope.runBlockingTest {
        val oneCallEntries = DummyJsonData.oneCallEntries
        oneCallEntries.forEach { entry ->
            `when`(remote.fetchOneCallWeatherByCoordinate(hanoi.coordinate)).thenReturn(entry)

            repo.fetchWeatherData(hanoi)

            // check
            val actualHanoi = cityDataSource.getCityById(hanoi.id)
                ?: throw IllegalStateException("city must not be null")

            entry.daily.forEach { expectedDailyWeather ->
                val actualDetailWeather =
                    repo.getDetailWeather(hanoi.id, expectedDailyWeather.dateTime).first()
                assertThat(actualHanoi, equalTo(actualDetailWeather.city))
                assertThat(actualDetailWeather.dailyWeather, `is`(expectedDailyWeather))
                val (startOfDay, endOfDay) = TimeUtils.getStartEndOfDay(expectedDailyWeather.dateTime)
                val expectedHourlyWeathers = entry.hourly
                    .filter { it.dateTime in startOfDay until endOfDay }
                    .map { it.copy(snow = null, rain = null) }
                val actualHourlyWeathers = actualDetailWeather.hourlyWeathers - entry.current
                assertThat(actualHourlyWeathers.size, equalTo(expectedHourlyWeathers.size))
                assertThat(
                    actualHourlyWeathers,
                    hasItems(*expectedHourlyWeathers.toTypedArray())
                )
            }
        }
    }
}
