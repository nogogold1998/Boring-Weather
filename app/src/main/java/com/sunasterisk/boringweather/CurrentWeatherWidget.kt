package com.sunasterisk.boringweather

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.sunasterisk.boringweather.base.Result
import com.sunasterisk.boringweather.data.model.CurrentWeather
import com.sunasterisk.boringweather.di.Injector
import com.sunasterisk.boringweather.util.TimeUtils
import com.sunasterisk.boringweather.util.UnitSystem
import com.sunasterisk.boringweather.util.defaultSharedPreferences

/**
 * Implementation of App Widget functionality.
 */
class CurrentWeatherWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val cityRepository = Injector.getCityRepository(context)
        val oneCallRepository = Injector.getOneCallRepository(context)
        val unitSystem = context.defaultSharedPreferences.unitSystem
        val selectedCityId = context.defaultSharedPreferences.selectedCityId
        val current = TimeUtils.getCurrentInSeconds()
        cityRepository.getCityById(selectedCityId) { cityResult ->
            if (cityResult is Result.Success) {
                oneCallRepository.getCurrentWeather(cityResult.data, current, false) {
                    if (it is Result.Success) {
                        for (appWidgetId in appWidgetIds) {
                            updateAppWidget(
                                context,
                                appWidgetManager,
                                appWidgetId,
                                it.data,
                                unitSystem
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onEnabled(context: Context) = Unit

    override fun onDisabled(context: Context) = Unit
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    currentWeather: CurrentWeather,
    unitSystem: UnitSystem
) {
    val currentHourly = currentWeather.currentWeather
    val resources = context.resources
    val views = RemoteViews(context.packageName, R.layout.widget_current_weather)
    with(views) {
        setTextViewText(R.id.text_widget_title, currentWeather.city.name)
        setTextViewText(
            R.id.text_widget_temperature,
            unitSystem.formatTemperature(currentHourly.temperature, resources)
        )
        setTextViewText(
            R.id.text_widget_feels_like,
            unitSystem.formatTemperature(currentHourly.feelsLike, resources, true)
        )
        setTextViewText(
            R.id.text_widget_description,
            currentHourly.weathers.firstOrNull()?.description
        )
        setTextViewText(
            R.id.text_widget_time,
            TimeUtils.formatToString(TimeUtils.FORMAT_TIME_SHORT, currentHourly.dateTime)
        )
    }

    appWidgetManager.updateAppWidget(appWidgetId, views)
}
