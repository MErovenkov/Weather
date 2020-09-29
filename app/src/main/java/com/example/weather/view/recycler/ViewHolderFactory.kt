package com.example.weather.view.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.model.WeatherCity

object ViewHolderFactory {
    fun create(view: View, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.w_rec_weather_current -> WeatherCurrentViewHolder(view)
            else -> WeatherFutureViewHolder(view)
        }
    }

}