package com.sunasterisk.boringweather.ui.search

import androidx.recyclerview.widget.DiffUtil
import com.sunasterisk.boringweather.ui.search.model.CityItem

class CityItemDiffCallback : DiffUtil.ItemCallback<CityItem>() {

    override fun areItemsTheSame(oldItem: CityItem, newItem: CityItem): Boolean =
        oldItem.data.id == newItem.data.id

    override fun areContentsTheSame(oldItem: CityItem, newItem: CityItem): Boolean =
        oldItem == newItem
}
