package com.example.weather.view.recycler

import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.WRecWeatherCurrentBinding
import com.example.weather.model.WeatherCity

class WeatherCurrentViewHolder(private val weatherCurrentBinding: WRecWeatherCurrentBinding):
    RecyclerView.ViewHolder(weatherCurrentBinding.root), GenericAdapter.Binder<WeatherCity> {

    override fun bind(data: WeatherCity) {
        itemView.apply {
            weatherCurrentBinding.wRecCityName.text = data.nameCity
            weatherCurrentBinding.wRecTemperatureWeather.text = data.weatherCurrent.temperature
        }
    }
}
