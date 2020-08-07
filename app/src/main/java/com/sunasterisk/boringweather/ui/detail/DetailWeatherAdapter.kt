package com.sunasterisk.boringweather.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.recyclerview.widget.ListAdapter
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.BaseViewHolder
import com.sunasterisk.boringweather.data.model.DailyWeather
import com.sunasterisk.boringweather.ui.detail.model.DailyWeatherItem
import com.sunasterisk.boringweather.ui.detail.model.DetailWeatherAdapterItem
import com.sunasterisk.boringweather.ui.detail.model.DetailWeatherAdapterItemDiffCallback
import com.sunasterisk.boringweather.ui.detail.model.HourlyWeatherItem
import com.sunasterisk.boringweather.util.TimeUtils
import com.sunasterisk.boringweather.util.UnitSystem
import com.sunasterisk.boringweather.util.load
import kotlinx.android.synthetic.main.item_weather_hourly_expandable.view.*
import kotlinx.android.synthetic.main.item_weather_hourly_expandable.view.textWeatherDescription
import kotlinx.android.synthetic.main.partial_detail.view.*
import kotlinx.android.synthetic.main.partial_summary.view.*

class DetailWeatherAdapter(
    private val unitSystem: UnitSystem,
    private val timeFormatString: String,
    private val clickListener: (Int) -> Unit
) : ListAdapter<DetailWeatherAdapterItem<*>, BaseViewHolder<*>>(
    DetailWeatherAdapterItemDiffCallback()
) {
    override fun getItemViewType(position: Int) = getItem(position).viewType.layoutRes

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(viewType, parent, false)
        return when (viewType) {
            DetailWeatherAdapterViewType.HOURLY_WEATHER.layoutRes ->
                HourlyWeatherVH(unitSystem, timeFormatString, view) { position, expanded ->
                    if (expanded) clickListener(position)
                    notifyItemChanged(position)
                }
            DetailWeatherAdapterViewType.DAILY_WEATHER.layoutRes ->
                DailyWeatherVH(unitSystem, view)
            else -> throw IllegalArgumentException("Wrong view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item: DetailWeatherAdapterItem<*> = getItem(position)
        if (holder is HourlyWeatherVH && item is HourlyWeatherItem) {
            holder.bind(item)
        } else if (holder is DailyWeatherVH && item is DailyWeatherItem) {
            holder.bind(item)
        }
    }

    class DailyWeatherVH(
        private val unitSystem: UnitSystem,
        view: View
    ) : BaseViewHolder<DailyWeatherItem>(view) {
        override fun bind(item: DailyWeatherItem) = with(itemView) {
            val data = item.data
            if (data.dateTime == DailyWeather.default.dateTime) return@with
            textDateTime.text = TimeUtils.formatToString(TimeUtils.FORMAT_DATE, data.dateTime)
            textCurrentTemperature.text =
                unitSystem.formatTemperature(data.temperature.average, resources)
            textWeatherDescription.text = data.weathers.firstOrNull()?.description ?: ""
            textFeelsLike.text = unitSystem.formatTemperature(
                data.temperature.run { max + min } / 2, resources
            )
            textSunrise.text = TimeUtils.formatToString(TimeUtils.FORMAT_TIME_SHORT, data.sunrise)
            textDayTemperature.text = unitSystem.formatTemperature(data.temperature.day, resources)
            textSunset.text = TimeUtils.formatToString(TimeUtils.FORMAT_TIME_SHORT, data.sunset)
            textNightTemperature.text =
                unitSystem.formatTemperature(data.temperature.night, resources)
            textTitleTile.visibility = View.GONE
            textHumidity.text =
                context.getString(R.string.format_percent_decimal, data.humidity)
            textVisibility.text = unitSystem.formatDistance(0, resources)
            textWindSpeed.text = unitSystem.formatSpeed(data.windSpeed, resources)
            textUVIndex.text = data.uvIndex.toString()
            textPressure.text = unitSystem.formatPressure(data.pressure, resources)
            textCloud.text = context.getString(R.string.format_percent_decimal, data.clouds)
        }
    }

    class HourlyWeatherVH(
        private val unitSystem: UnitSystem,
        private val timeFormatString: String,
        view: View,
        private val expandClick: (Int, Boolean) -> Unit
    ) : BaseViewHolder<HourlyWeatherItem>(view) {

        private var hourlyWeatherItem: HourlyWeatherItem? = null

        init {
            itemView.setOnClickListener {
                hourlyWeatherItem?.let {
                    it.expanded = !it.expanded
                    expandClick(adapterPosition, it.expanded)
                    animationPos = adapterPosition
                }
            }
        }

        override fun bind(item: HourlyWeatherItem) = with(itemView) {
            val data = item.data
            hourlyWeatherItem = item

            textTime.text = TimeUtils.formatToString(timeFormatString, data.dateTime)

            context.getString(R.string.format_url_weather_icon, data.weathers.firstOrNull()?.icon)
                .let { imageWeatherIcon?.load(it) }
            textWeatherDescription.text = data.weathers.firstOrNull()?.description ?: ""
            textTitleTile.visibility = View.GONE
            textHumidity.text =
                context.getString(R.string.format_percent_decimal, data.humidity)
            textVisibility.text = unitSystem.formatDistance(data.visibility, resources)
            textWindSpeed.text = unitSystem.formatSpeed(data.windSpeed, resources)
            textUVIndex.text =
                data.uvIndex?.toString() ?: context.getString(R.string.title_holder_float_number)
            textPressure.text = unitSystem.formatPressure(data.pressure, resources)
            textCloud.text = context.getString(R.string.format_percent_decimal, data.clouds)
            containerDetail.visibility = if (item.expanded) View.VISIBLE else View.GONE
            rotateAnimateExpandButton(item)
        }

        private fun rotateAnimateExpandButton(item: HourlyWeatherItem, degrees: Float = 180f) {
            if (adapterPosition == animationPos) {
                val degreeOffset = itemView.buttonExpand.rotation
                itemView.buttonExpand.animation = RotateAnimation(
                    (if (item.expanded) 0f else degrees) - degreeOffset,
                    (if (item.expanded) degrees else 0f) - degreeOffset,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
                ).apply {
                    duration = 500
                    repeatCount = 0
                    repeatMode = Animation.REVERSE
                    fillAfter = true
                }
                animationPos = -1
            } else {
                itemView.buttonExpand.rotation = if (item.expanded) degrees else 0f
            }
        }

        companion object {
            private var animationPos: Int = -1
        }
    }
}

