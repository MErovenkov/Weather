package com.merovenkov.weather.ui.recycler

import androidx.recyclerview.widget.RecyclerView

interface ItemTouchHelperAdapter {
    fun onClickItem(holder: RecyclerView.ViewHolder, position: Int)
    fun onItemDismiss(position: Int)
    fun <T> itemDismiss(data: T)
}