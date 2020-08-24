package com.sunasterisk.boringweather.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sunasterisk.boringweather.R
import com.sunasterisk.boringweather.base.BaseViewHolder
import com.sunasterisk.boringweather.data.model.City
import com.sunasterisk.boringweather.ui.search.model.CityItem
import com.sunasterisk.boringweather.ui.search.model.isFetched
import kotlinx.android.synthetic.main.item_city_search.view.*

class CityAdapter(
    private val onclickListener: (City) -> Unit,
    private val onBookMarkListener: (Int) -> Unit
) : ListAdapter<CityItem, CityAdapter.CityVH>(CityItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityVH {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_city_search, parent, false)
        return CityVH(view, onclickListener) {
            onBookMarkListener(it)
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: CityVH, position: Int) {
        holder.bind(getItem(position))
    }

    class CityVH(
        view: View, onclickListener: (City) -> Unit, onBookMarkListener: (Int) -> Unit
    ) : BaseViewHolder<CityItem>(view) {
        private var cityItem: CityItem? = null

        init {
            view.linearContainerCity.setOnClickListener { cityItem?.data?.let(onclickListener) }
            view.imageBookMark.setOnClickListener { cityItem?.data?.id?.let(onBookMarkListener) }
        }

        override fun bind(item: CityItem): Unit = with(itemView) {
            cityItem = item
            textCountry.text = item.data.country
            textCity.text = item.data.name

            with(imageBookMark) {
                visibility = View.VISIBLE
                when {
                    item.isBookMarked -> setImageResource(R.drawable.ic_round_bookmark_24)
                    item.data.isFetched -> setImageResource(R.drawable.ic_round_bookmark_border_24)
                    else -> visibility = View.INVISIBLE
                }
            }
        }
    }
}
