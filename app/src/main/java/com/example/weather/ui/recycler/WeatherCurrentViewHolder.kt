package com.example.weather.ui.recycler

import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.RecyclerWeatherBinding
import com.example.weather.data.model.WeatherCity

class WeatherCurrentViewHolder(private val recyclerWeatherBinding: RecyclerWeatherBinding):
    RecyclerView.ViewHolder(recyclerWeatherBinding.root), GenericAdapter.Binder<WeatherCity> {

    override fun bind(data: WeatherCity) {
        itemView.apply {
            recyclerWeatherBinding.cityName.text = data.nameCity
            recyclerWeatherBinding.temperatureWeather.text = data.weatherCurrent.temperature
        }
    }
}
