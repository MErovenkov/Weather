package com.example.weather.ui.recycler

import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.RecyclerDetailedWeatherBinding
import com.example.weather.model.WeatherFuture

class WeatherFutureViewHolder(private val recyclerDetailedWeatherBinding: RecyclerDetailedWeatherBinding):
    RecyclerView.ViewHolder(recyclerDetailedWeatherBinding.root), GenericAdapter.Binder<WeatherFuture> {

    override fun bind(data: WeatherFuture) {
        itemView.apply {
            recyclerDetailedWeatherBinding.dayWeek.text = data.nameDay
            recyclerDetailedWeatherBinding.temperatureMax.text = data.temperatureMax
            recyclerDetailedWeatherBinding.temperatureMin.text = data.temperatureMin
            recyclerDetailedWeatherBinding.iconWeather.setImageResource(resources
                .getIdentifier("ic_future_w${data.nameIconWeather}","drawable",
                    "com.example.weather"))
        }
    }
}