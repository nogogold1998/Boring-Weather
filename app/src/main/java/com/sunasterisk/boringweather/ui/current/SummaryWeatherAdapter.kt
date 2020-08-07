package com.sunasterisk.boringweather.ui.current

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.BaseViewHolder
import com.sunasterisk.boringweather.data.model.SummaryWeather
import com.sunasterisk.boringweather.util.TimeUtils
import com.sunasterisk.boringweather.util.UnitSystem
import com.sunasterisk.boringweather.util.load
import kotlinx.android.synthetic.main.item_summary_weather.view.*

class SummaryWeatherAdapter(
    private val unitSystem: UnitSystem,
    private val timeFormatString: String,
    private val itemClickListener: (SummaryWeather) -> Unit
) : ListAdapter<SummaryWeather, SummaryWeatherAdapter.SummaryWeatherVH>(
    SummaryWeather.diffUtil
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryWeatherVH {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_summary_weather, parent, false)
        return SummaryWeatherVH(view, unitSystem, timeFormatString, itemClickListener)
    }

    override fun onBindViewHolder(holder: SummaryWeatherVH, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class SummaryWeatherVH(
        view: View,
        private val unitSystem: UnitSystem,
        private val timeFormatString: String,
        private val itemClickListener: (SummaryWeather) -> Unit
    ) : BaseViewHolder<SummaryWeather>(view) {
        private var holdingItem: SummaryWeather? = null

        init {
            itemView.containerItem.setOnClickListener { holdingItem?.let(itemClickListener) }
        }

        override fun bind(item: SummaryWeather): Unit = with(itemView) {
            holdingItem = item
            textTemperature.text = unitSystem.formatTemperature(item.temperature, resources)
            textDateTime.text = TimeUtils.formatToString(timeFormatString, item.dt)
            item.icon?.let {
                imageWeather?.load(context.getString(R.string.format_url_weather_icon, it))
            }
        }
    }
}
