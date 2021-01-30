package com.example.weather.ui.recycler

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.data.model.WeatherCity

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
        onClickItem(holder, position)
    }

    internal interface Binder<T> {
        fun bind(data: T)
    }

    override fun onClickItem(holder: RecyclerView.ViewHolder, position: Int) {}

    @Suppress("UNCHECKED_CAST")
    fun <T> getItem(position: Int): T {
        return itemList[position] as T
    }

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int = getLayoutId(itemList[position])

    private fun getLayoutId(obj: T): Int {
        return when(obj) {
            is WeatherCity -> R.layout.recycler_weather
            else ->  R.layout.recycler_detailed_weather
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