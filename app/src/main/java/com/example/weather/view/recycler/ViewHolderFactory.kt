package com.example.weather.view.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.DwRecWeatherFutureBinding
import com.example.weather.databinding.WRecWeatherCurrentBinding

object ViewHolderFactory {
    fun create(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.w_rec_weather_current -> WeatherCurrentViewHolder(WRecWeatherCurrentBinding
                .inflate(layoutInflater, parent, false))
            else -> WeatherFutureViewHolder(DwRecWeatherFutureBinding
                .inflate(layoutInflater, parent, false))
        }
    }
}