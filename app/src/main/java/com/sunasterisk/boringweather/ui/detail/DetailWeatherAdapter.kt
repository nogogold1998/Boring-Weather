package com.sunasterisk.boringweather.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.recyclerview.widget.ListAdapter
import com.sunasterisk.boringweather.base.BaseViewHolder
import com.sunasterisk.boringweather.ui.detail.model.DailyWeatherItem
import com.sunasterisk.boringweather.ui.detail.model.DetailWeatherAdapterItem
import com.sunasterisk.boringweather.ui.detail.model.DetailWeatherAdapterItemDiffCallback
import com.sunasterisk.boringweather.ui.detail.model.HourlyWeatherItem
import kotlinx.android.synthetic.main.item_weather_hourly_expandable.view.*

class DetailWeatherAdapter(
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
                HourlyWeatherVH(view) { position: Int, expanded: Boolean ->
                    if (expanded) clickListener(position)
                    notifyItemChanged(position)
                }
            DetailWeatherAdapterViewType.DAILY_WEATHER.layoutRes -> DailyWeatherVH(view)
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

    class DailyWeatherVH(view: View) : BaseViewHolder<DailyWeatherItem>(view) {
        override fun bind(item: DailyWeatherItem) {
            // TODO bind data here
        }
    }

    class HourlyWeatherVH(
        view: View,
        private val expandClick: (Int, Boolean) -> Unit
    ) : BaseViewHolder<HourlyWeatherItem>(view) {

        private var hourlyWeatherItem: HourlyWeatherItem? = null

        init {
            itemView.buttonExpand.setOnClickListener {
                hourlyWeatherItem?.let {
                    it.expanded = !it.expanded
                    expandClick(adapterPosition, it.expanded)
                    animationPos = adapterPosition
                }
            }
        }

        override fun bind(item: HourlyWeatherItem) {
            hourlyWeatherItem = item
            with(itemView) {
                textTime.text = item.data.dateTime.toString() // TODO bind data here
                containerDetail.visibility = if (item.expanded) View.VISIBLE else View.GONE
            }
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

