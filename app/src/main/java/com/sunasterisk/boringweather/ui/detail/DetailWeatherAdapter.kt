package com.sunasterisk.boringweather.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.recyclerview.widget.ListAdapter
import com.sunasterisk.boringweather.base.BaseViewHolder
import com.sunasterisk.boringweather.databinding.ItemWeatherDailyHeaderBinding
import com.sunasterisk.boringweather.databinding.ItemWeatherHourlyExpandableBinding
import com.sunasterisk.boringweather.ui.detail.model.DailyWeatherItem
import com.sunasterisk.boringweather.ui.detail.model.DetailWeatherAdapterItem
import com.sunasterisk.boringweather.ui.detail.model.DetailWeatherAdapterItemDiffCallback
import com.sunasterisk.boringweather.ui.detail.model.HourlyWeatherItem
import com.sunasterisk.boringweather.util.UnitSystem
import kotlinx.android.synthetic.main.item_weather_hourly_expandable.view.*

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
        unitSystem: UnitSystem,
        view: View,
    ) : BaseViewHolder<DailyWeatherItem>(view) {
        private val binding = ItemWeatherDailyHeaderBinding.bind(view).also {
            it.unitSystem = unitSystem
        }

        override fun bind(item: DailyWeatherItem) = with(binding) {
            dailyWeather = item.data
            executePendingBindings()
        }
    }

    class HourlyWeatherVH(
        unitSystem: UnitSystem,
        timeFormatString: String,
        view: View,
        private val expandClick: (Int, Boolean) -> Unit,
    ) : BaseViewHolder<HourlyWeatherItem>(view) {
        private val binding = ItemWeatherHourlyExpandableBinding.bind(view).apply {
            this.unitSystem = unitSystem
            this.timeFormatString = timeFormatString
            root.setOnClickListener { _ ->
                hourlyWeatherItem?.let { item ->
                    item.expanded = !item.expanded
                    expandClick(adapterPosition, item.expanded)
                    animationPos = adapterPosition
                }
            }
        }

        private var hourlyWeatherItem: HourlyWeatherItem? = null

        override fun bind(item: HourlyWeatherItem) = with(binding) {
            hourlyWeatherItem = item
            hourlyWeather = item.data
            isExpanded = item.expanded
            executePendingBindings()
            containerDetail.textTitleTile.visibility = View.GONE
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

