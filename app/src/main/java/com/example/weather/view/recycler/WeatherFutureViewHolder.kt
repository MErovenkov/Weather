package com.example.weather.view.recycler

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.model.WeatherFuture

class WeatherFutureViewHolder(view: View): RecyclerView.ViewHolder(view), GenericAdapter.Binder<WeatherFuture> {
    private var nameDay: TextView = view.findViewById(R.id.dw_rec_day_week)
    private var temperatureMax: TextView = view.findViewById(R.id.dw_rec_temperature_max)
    private var temperatureMin: TextView = view.findViewById(R.id.dw_rec_temperature_min)
    private var iconWeatherFuture: ImageView = view.findViewById(R.id.dw_rec_icon_weather)

    override fun bind(data: WeatherFuture) {
        itemView.apply {
            nameDay.text = data.nameDay
            temperatureMax.text = data.temperatureMax
            temperatureMin.text = data.temperatureMin
            iconWeatherFuture.setImageResource(resources
                .getIdentifier(data.nameIconWeather,"drawable",
                    "com.example.weather"))
        }
    }
}
