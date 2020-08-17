package com.sunasterisk.boringweather.data.source.local.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sunasterisk.boringweather.DummyJsonData
import com.sunasterisk.boringweather.data.model.Coordinate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CityDaoTest {
    private lateinit var appDatabase: AppRoomDatabase
    private lateinit var cityDao: CityDao

    private val prepopulateCities = DummyJsonData.cities

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
        val hanoi = DummyJsonData.hanoi
        val haiphong = DummyJsonData.haiphong
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
            assert(setOf(hanoi, haiphong).all(allCities::contains))
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
        val hanoi = DummyJsonData.hanoi
        cityDao.insertCity(hanoi)

        run {
            val actual = cityDao.getCityByCoordinate(hanoi.coordinate)
            assertThat(actual, `is`(hanoi))
        }

        repeat(50) {
            val (lon, lat) = hanoi.coordinate
            val random = { seed: Float, scale: Int ->
                (seed + (Math.random() - 0.5) * (Math.random() / scale)).toFloat()
            }
            val actual = cityDao.getCityByCoordinate(Coordinate(random(lon, 10), random(lat, 10)))
            assertThat(actual, `is`(hanoi))
        }
    }

    @Test
    fun getCityById() = runBlockingTest {
        val hanoi = DummyJsonData.hanoi
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
        val hanoi = DummyJsonData.hanoi
        cityDao.insertCity(hanoi)
        // given
        val names = listOf(hanoi.name, "Ha", "Ha noi", "ha noi", "noi", " ", "", "a", "n", "oi")
        names.forEach {
            // when
            val actualCities = cityDao.findCityByName(it, 100).first()
            // then
            assert(actualCities.contains(hanoi))
            println("with: $it, result count=${actualCities.size}")
        }
    }
}