package com.example.weather.view.recycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.model.WeatherCity

class WeatherCurrentViewHolder(view: View): RecyclerView.ViewHolder(view), GenericAdapter.Binder<WeatherCity> {
    private var nameCity: TextView = view.findViewById(R.id.w_rec_city_name)
    private var temperatureWeatherCurrent: TextView = view.findViewById(R.id.w_rec_temperature_weather)

    override fun bind(data: WeatherCity) {
        itemView.apply {
            nameCity.text = data.nameCity
            temperatureWeatherCurrent.text = data.weatherCurrent.temperature
        }
    }
}
