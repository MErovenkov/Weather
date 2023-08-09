package com.merovenkov.weather.ui.recycler

import androidx.recyclerview.widget.RecyclerView
import com.merovenkov.weather.databinding.RecyclerWeatherBinding
import com.merovenkov.weather.data.model.WeatherCity

class WeatherCurrentViewHolder(private val recyclerWeatherBinding: RecyclerWeatherBinding):
    RecyclerView.ViewHolder(recyclerWeatherBinding.root), GenericAdapter.Binder<WeatherCity> {

    override fun bind(data: WeatherCity) {
        itemView.apply {
            recyclerWeatherBinding.cityName.text = data.nameCity
            recyclerWeatherBinding.temperatureWeather.text = data.weatherCurrent.temperature
        }
    }
}
