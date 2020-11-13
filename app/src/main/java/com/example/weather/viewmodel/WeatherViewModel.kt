package com.example.weather.viewmodel

import androidx.lifecycle.*
import com.example.weather.repository.Repository
import com.example.weather.model.WeatherCity
import com.example.weather.utils.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: Repository): ViewModel() {

    private var resource: MutableStateFlow<Resource<ArrayList<WeatherCity>>>
            = MutableStateFlow(Resource(repository.getWeatherCities()))

    fun getResource(): StateFlow<Resource<ArrayList<WeatherCity>>> = resource.asStateFlow()

    fun createWeatherData(nameCity: String) {
        viewModelScope.launch {
            repository.createWeatherCity(nameCity).collect {
                resource.value = it
            }
        }
    }

    fun updateWeatherData() {
        viewModelScope.launch {
            repository.updateAllCitiesWeather().collect {
                resource.value = it
            }
        }
    }

    fun deleteWeatherCity(weatherCity: WeatherCity) {
        repository.deletedWeatherCity(weatherCity)
    }
}