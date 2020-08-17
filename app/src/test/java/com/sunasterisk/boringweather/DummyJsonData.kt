package com.sunasterisk.boringweather

import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.data.model.Coordinate
import com.sunasterisk.boringweather.data.model.DailyWeather
import com.sunasterisk.boringweather.data.model.HourlyWeather
import com.sunasterisk.boringweather.data.model.OneCallEntry
import com.sunasterisk.boringweather.data.model.Temperature
import com.sunasterisk.boringweather.data.model.Weather

object DummyJsonData {
    val weather1 = Weather(804, "Clouds", "overcast clouds", "04n") to """{
        "id": 804,
        "main": "Clouds",
        "description": "overcast clouds",
        "icon": "04n"
      }"""
    val weather2 = Weather(500, "Rain", "light rain", "10d") to """{
          "id": 500,
          "main": "Rain",
          "description": "light rain",
          "icon": "10d"
        }"""
    val current = HourlyWeather(
        1597592220,
        299.5f, 303.3f,
        1006,
        87,
        297.16f,
        100,
        2.89f, 133,
        listOf(weather1.first),
        10000,
        null, null, null,
        13.57f
    ) to """{
    "dt": 1597592220,
    "sunrise": 1597530954,
    "sunset": 1597577172,
    "temp": 299.5,
    "feels_like": 303.3,
    "pressure": 1006,
    "humidity": 87,
    "dew_point": 297.16,
    "uvi": 13.57,
    "clouds": 100,
    "visibility": 10000,
    "wind_speed": 2.89,
    "wind_deg": 133,
    "weather": [
      {
        "id": 804,
        "main": "Clouds",
        "description": "overcast clouds",
        "icon": "04n"
      }
    ]
  }"""
    val hourly1 = HourlyWeather(
        1597590000,
        299.5f, 303.3f,
        1006,
        87,
        297.16f,
        100,
        2.89f, 133,
        listOf(weather1.first),
        10000,
        null, null, null, null
    ) to """
        {
      "dt": 1597590000,
      "temp": 299.5,
      "feels_like": 303.3,
      "pressure": 1006,
      "humidity": 87,
      "dew_point": 297.16,
      "clouds": 100,
      "visibility": 10000,
      "wind_speed": 2.89,
      "wind_deg": 133,
      "weather": [
        {
          "id": 804,
          "main": "Clouds",
          "description": "overcast clouds",
          "icon": "04n"
        }
      ],
      "pop": 0.53
    }
    """.trimIndent()
    val temperature1 = Temperature(
        301.96f,
        296.9f,
        303.3f,
        299.42f,
        303.3f,
        296.9f
    ) to """{
        "day": 301.96,
        "min": 296.9,
        "max": 303.3,
        "night": 299.42,
        "eve": 303.3,
        "morn": 296.9
      }"""
    val daily1 = DailyWeather(
        1598155200,
        1598135884, 1598181652,
        temperature1.first,
        1008,
        75,
        297.13f,
        2.88f,
        178,
        listOf(weather2.first),
        90,
        14.17f
    ) to """{
      "dt": 1598155200,
      "sunrise": 1598135884,
      "sunset": 1598181652,
      "temp": {
        "day": 301.96,
        "min": 296.9,
        "max": 303.3,
        "night": 299.42,
        "eve": 303.3,
        "morn": 296.9
      },
      "feels_like": {
        "day": 305.72,
        "night": 301.11,
        "eve": 304.38,
        "morn": 300.85
      },
      "pressure": 1008,
      "humidity": 75,
      "dew_point": 297.13,
      "wind_speed": 2.88,
      "wind_deg": 178,
      "weather": [
        {
          "id": 500,
          "main": "Rain",
          "description": "light rain",
          "icon": "10d"
        }
      ],
      "clouds": 90,
      "pop": 0.64,
      "rain": 1.72,
      "uvi": 14.17
    }""".trimIndent()
    val oneCallEntry = OneCallEntry(
        21.02f, 105.84f,
        "Asia/Bangkok", 25200,
        current.first,
        emptyList(),
        emptyList()
    ) to TestHelper.readContentFromFilePath(TestHelper.ONE_CALL_JSON_FILE_PATH)

    val hanoi = City(
        1581130,
        "Ha Noi",
        "VN",
        City.default.lastFetch,
        Coordinate(105.841171f, 21.0245f)
    )
}
