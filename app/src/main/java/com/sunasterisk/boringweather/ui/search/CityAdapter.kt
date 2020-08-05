package com.sunasterisk.boringweather.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.BaseViewHolder
import com.sunasterisk.boringweather.data.model.City
import kotlinx.android.synthetic.main.item_city_search.view.*

class CityAdapter(
    private val onclickListener: (Int) -> Unit
) : ListAdapter<City, CityAdapter.CityVH>(CityAdapterItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityVH {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_city_search, parent, false)
        return CityVH(view, onclickListener)
    }

    override fun onBindViewHolder(holder: CityVH, position: Int) {
        holder.bind(getItem(position))
    }

    class CityVH(view: View, onclickListener: (Int) -> Unit) : BaseViewHolder<City>(view) {
        private var cityId: Int? = null

        init {
            view.linearContainerCity.setOnClickListener {
                cityId?.let(onclickListener)
            }
        }

        override fun bind(item: City) = with(itemView) {
            cityId = item.id
            textCountry.text = item.country
            textCity.text = item.name
        }
    }
}
