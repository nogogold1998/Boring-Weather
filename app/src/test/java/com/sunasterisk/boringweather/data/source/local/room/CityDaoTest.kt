package com.sunasterisk.boringweather.data.source.local.room

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sunasterisk.boringweather.DummyJsonData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class CityDaoTest {
    private lateinit var appDatabase: AppRoomDatabase
    private lateinit var cityDao: CityDao

    private val prepopulateCities = DummyJsonData.cities
    private val hanoi = DummyJsonData.hanoi
    private val haiphong = DummyJsonData.haiphong

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().context.applicationContext
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        cityDao = appDatabase.cityDao()
        cityDao.insertCity(*prepopulateCities.toTypedArray())
    }

    @After
    fun teardown() {
        appDatabase.close()
    }

    @Test
    fun insertCity() = runBlockingTest {
        // given
        cityDao.getAllCities().first().let {
            assertThat(it.size, equalTo(prepopulateCities.size))
        }
        // when
        cityDao.insertCity(hanoi)
        cityDao.getAllCities().first().let { allCities ->
            // then
            assertThat(allCities.size, equalTo(prepopulateCities.size + 1))
            assert(allCities.contains(hanoi))
        }

        // when
        cityDao.insertCity(DummyJsonData.haiphong)
        cityDao.getAllCities().first().let { allCities ->
            // then
            assertThat(allCities.size, equalTo(prepopulateCities.size + 2))
            assert(allCities.containsAll(setOf(hanoi, haiphong)))
        }

        // when
        cityDao.insertCity(DummyJsonData.hanoi)
        cityDao.getAllCities().first().let { allCities ->
            // then
            assertThat(allCities.size, equalTo(prepopulateCities.size + 2))
            assert(setOf(hanoi, haiphong).all(allCities::contains))
            println(allCities)
        }
    }

    @Test
    fun getCityByCoordinate() = runBlockingTest {
        cityDao.insertCity(hanoi)

        run {
            val actual =
                cityDao.getCityByLatLon(hanoi.coordinate.latitude, hanoi.coordinate.longitude)
            assertThat(actual, hasItems(hanoi))
        }

        repeat(50) {
            val (lon, lat) = hanoi.coordinate
            val random = { seed: Float, scale: Int ->
                (seed + (Math.random() - 0.5) * (Math.random() / scale)).toFloat()
            }
            val actual = cityDao.getCityByLatLon(random(lat, 10), random(lon, 10))
            assertThat(actual, hasItems(hanoi))
        }
    }

    @Test
    fun getCityById() = runBlockingTest {
        val expectedCities = DummyJsonData.cities
        expectedCities.forEach { expected ->
            val actual = cityDao.getCityById(expected.id)
            assertThat(actual, equalTo(expected))
        }

        val actual = cityDao.getCityById(hanoi.id)
        assert(actual == null)
    }

    @Test
    fun findCityByName() = runBlockingTest {
        cityDao.insertCity(hanoi)
        // given
        val names = listOf(hanoi.name, "Ha", "Ha noi", "ha noi", "noi", " ", "", "a", "n", "oi")
        names.forEach {
            // when
            val actualCities = cityDao.findCityByName(it, 100)
            println("with: $it, result count=${actualCities.size}")
            // then
            assertThat(actualCities, hasItems(hanoi))
        }
    }

    @Test
    fun getFetchedCities() = runBlockingTest {
        val hanoi = hanoi.copy(lastFetch = 1)
        val haiphong = haiphong.copy(lastFetch = 1)

        cityDao.insertCity(hanoi, haiphong)

        val actual = cityDao.getFetchedCities().first()
        assertThat(actual, containsInAnyOrder(hanoi, haiphong))
    }

    @Test
    fun updateFetchedCity() = runBlockingTest {
        cityDao.insertCity(hanoi)

        val hanoi = hanoi.copy(lastFetch = 2000)
        cityDao.updateFetchedCity(hanoi.id, hanoi.lastFetch)

        val actual = cityDao.getFetchedCities().first()
        assertThat(actual, contains(hanoi))
    }
}
