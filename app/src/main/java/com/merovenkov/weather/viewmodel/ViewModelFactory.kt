package com.merovenkov.weather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.merovenkov.weather.data.repository.Repository

class ViewModelFactory(private val repository: Repository
    ) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass) {
            WeatherViewModel::class.java ->
                WeatherViewModel(repository) as T

            DetailedWeatherViewModel::class.java ->
                DetailedWeatherViewModel(repository) as T

            PrecipitationMapViewModel::class.java ->
                PrecipitationMapViewModel(repository) as T

            else -> super.create(modelClass)
        }
    }
}