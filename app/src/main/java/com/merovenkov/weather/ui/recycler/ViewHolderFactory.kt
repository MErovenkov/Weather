package com.merovenkov.weather.ui.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.merovenkov.weather.R
import com.merovenkov.weather.databinding.RecyclerDetailedWeatherBinding
import com.merovenkov.weather.databinding.RecyclerWeatherBinding

object ViewHolderFactory {
    fun create(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.recycler_weather -> WeatherCurrentViewHolder(RecyclerWeatherBinding
                .inflate(layoutInflater, parent, false))
            else -> WeatherFutureViewHolder(RecyclerDetailedWeatherBinding
                .inflate(layoutInflater, parent, false))
        }
    }
}