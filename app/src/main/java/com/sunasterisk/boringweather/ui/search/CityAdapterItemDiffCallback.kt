package com.sunasterisk.boringweather.ui.search

import androidx.recyclerview.widget.DiffUtil
import com.sunasterisk.boringweather.data.model.City

class CityAdapterItemDiffCallback : DiffUtil.ItemCallback<City>() {

    override fun areItemsTheSame(oldItem: City, newItem: City): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: City, newItem: City): Boolean =
        oldItem == newItem
}
