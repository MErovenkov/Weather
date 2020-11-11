package com.example.weather.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.weather.repository.Repository
import com.example.weather.model.WeatherCity

class WeatherViewModel (private val repository: Repository): EventStatusViewModel(repository) {

    private var weatherCities : MediatorLiveData<ArrayList<WeatherCity>> = MediatorLiveData()

    init {
        weatherCities.addSource(repository.getWeatherCities()) {
            weatherCities.postValue(it)
        }
    }

    fun getWeatherCities(): LiveData<ArrayList<WeatherCity>> = weatherCities

    fun createWeatherData(nameCity: String) {
        repository.createWeatherCity(nameCity)
    }

    fun updateWeatherData() {
        repository.updateAllCitiesWeather()
    }

    fun deleteWeatherCity(weatherCity: WeatherCity) {
        repository.deletedWeatherCity(weatherCity)
    }
}