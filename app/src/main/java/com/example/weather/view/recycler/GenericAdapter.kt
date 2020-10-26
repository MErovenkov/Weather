package com.example.weather.view.recycler

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.model.WeatherCity

abstract class GenericAdapter<T>
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {
    private var itemList = ArrayList<T>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
          return getViewHolder(parent, viewType)
    }

    protected open fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderFactory.create(view as ViewGroup, viewType)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Binder<T>).bind(itemList[position])
    }

    internal interface Binder<T> {
        fun bind(data: T)
    }

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int = getLayoutId(itemList[position])

    private fun getLayoutId(obj: T): Int {
        return when(obj) {
            is WeatherCity -> R.layout.w_rec_weather_current
            else ->  R.layout.dw_rec_weather_future
        }
    }

    override fun onItemDismiss(position: Int) {
        itemDismiss(itemList[position])
        itemList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    override fun <T> itemDismiss(data: T) {}

    fun update(items: ArrayList<T>) {
        itemList.clear();
        itemList = items
        notifyDataSetChanged()
    }
}