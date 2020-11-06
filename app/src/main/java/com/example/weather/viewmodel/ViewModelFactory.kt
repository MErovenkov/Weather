package com.example.weather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.repository.Repository

class ViewModelFactory(private val repository: Repository
    ) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass) {
            WeatherViewModel::class.java ->
                WeatherViewModel(repository) as T
            DetailedWeatherViewModel::class.java ->
                DetailedWeatherViewModel(repository) as T
            else -> super.create(modelClass)
        }
    }
}