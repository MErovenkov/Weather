package com.merovenkov.weather.ui.recycler

import androidx.recyclerview.widget.RecyclerView
import com.merovenkov.weather.databinding.RecyclerDetailedWeatherBinding
import com.merovenkov.weather.data.model.WeatherFuture

class WeatherFutureViewHolder(private val recyclerDetailedWeatherBinding: RecyclerDetailedWeatherBinding):
    RecyclerView.ViewHolder(recyclerDetailedWeatherBinding.root), GenericAdapter.Binder<WeatherFuture> {

    override fun bind(data: WeatherFuture) {
        itemView.apply {
            recyclerDetailedWeatherBinding.dayWeek.text = data.nameDay
            recyclerDetailedWeatherBinding.temperatureMax.text = data.temperatureMax
            recyclerDetailedWeatherBinding.temperatureMin.text = data.temperatureMin
            recyclerDetailedWeatherBinding.iconWeather.setImageResource(resources
                .getIdentifier("ic_w${data.nameIconWeather}","drawable",
                    "com.merovenkov.weather"))
        }
    }
}