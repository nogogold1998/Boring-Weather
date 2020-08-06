package com.sunasterisk.boringweather.ui.current

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.data.model.SummaryWeather
import com.sunasterisk.boringweather.util.TimeUtils
import com.sunasterisk.boringweather.util.UnitSystem
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
    ) : RecyclerView.ViewHolder(view) {

        fun bind(item: SummaryWeather) {
            with(itemView) {
                textTemperature.text = unitSystem.formatTemperature(item.temperature, resources)
                textDateTime.text = TimeUtils.formatToString(timeFormatString, item.dt)
                containerItem.setOnClickListener { itemClickListener(item) }
            }
        }
    }
}
