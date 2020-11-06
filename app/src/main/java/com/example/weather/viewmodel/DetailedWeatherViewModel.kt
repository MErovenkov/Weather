package com.example.weather.viewmodel

import androidx.lifecycle.MediatorLiveData
import com.example.weather.repository.Repository
import com.example.weather.model.WeatherCity

class DetailedWeatherViewModel (private val repository: Repository): EventStatusViewModel(repository) {

    private var weatherCity: MediatorLiveData<WeatherCity> = MediatorLiveData()

    fun initLiveData(nameCity: String) {
        weatherCity.addSource(repository.getWeatherCities()) {
            weatherCity.value = it.first(){city -> city.nameCity == nameCity}
        }
    }

    fun getWeatherCity() = weatherCity

    fun updateWeatherData() {
        repository.updateWeatherCity(weatherCity.value!!)
    }
}