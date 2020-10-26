package com.example.weather.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.dao.OrmLiteHelper
import com.example.weather.api.WeatherData

class ViewModelFactory(private val application: Application,
                       private val dataBaseHelper: OrmLiteHelper,
                       private val weatherData: WeatherData) :
    ViewModelProvider.AndroidViewModelFactory(application) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass) {
            WeatherViewModel::class.java ->
                WeatherViewModel(application, dataBaseHelper, weatherData) as T
            DetailedWeatherViewModel::class.java ->
                DetailedWeatherViewModel(application, dataBaseHelper, weatherData) as T
            else -> super.create(modelClass)
        }
    }
}