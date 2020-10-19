package com.example.weather.view.recycler

import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.DwRecWeatherFutureBinding
import com.example.weather.model.WeatherFuture

class WeatherFutureViewHolder(private val weatherFutureBinding: DwRecWeatherFutureBinding):
    RecyclerView.ViewHolder(weatherFutureBinding.root), GenericAdapter.Binder<WeatherFuture> {

    override fun bind(data: WeatherFuture) {
        itemView.apply {
            weatherFutureBinding.dwRecDayWeek.text = data.nameDay
            weatherFutureBinding.dwRecTemperatureMax.text = data.temperatureMax
            weatherFutureBinding.dwRecTemperatureMin.text = data.temperatureMin
            weatherFutureBinding.dwRecIconWeather.setImageResource(resources
                .getIdentifier("ic_future_w${data.nameIconWeather}","drawable",
                    "com.example.weather"))
        }
    }
}
