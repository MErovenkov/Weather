package com.example.weather.view.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.model.WeatherCity

abstract class GenericAdapter<T>
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {
    private var itemList = ArrayList<T>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return getViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(viewType, parent, false), viewType
        )
    }

    protected open fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderFactory.create(view, viewType)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Binder<T>).bind(itemList[position])
    }

    internal interface Binder<T> {
        fun bind(data: T)
    }

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int = getLayoutId(position, itemList[position])

    private fun getLayoutId(@Suppress("UNUSED_PARAMETER") position: Int, obj: T): Int {
        return when(obj) {
            is WeatherCity -> R.layout.w_rec_weather_current
            else ->  R.layout.dw_rec_weather_future
        }
    }

    override fun onItemDismiss(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount);
    }

    fun getItemList(): MutableList<T> {
        return itemList
    }

    fun update(items: ArrayList<T>) {
        itemList.clear();
        itemList = items
        notifyDataSetChanged()
    }

    fun addItem(item: T) {
        itemList.add(item)
        notifyItemInserted(itemCount - 1)
    }
}