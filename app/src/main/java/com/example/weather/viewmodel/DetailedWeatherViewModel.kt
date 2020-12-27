package com.example.weather.viewmodel

import androidx.lifecycle.*
import com.example.weather.repository.Repository
import com.example.weather.model.WeatherCity
import com.example.weather.utils.EventStatus
import com.example.weather.utils.Resource
import com.example.weather.utils.extensions.getData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailedWeatherViewModel(private val repository: Repository): ViewModel() {

    private lateinit var resource: MutableStateFlow<Resource<WeatherCity>>

    fun initLiveData(nameCity: String, isCurrentLocation: Boolean) {
        resource = if (isCurrentLocation) {
            MutableStateFlow(Resource(repository.getCurrentLocationWeather()))
        } else {
            MutableStateFlow(Resource(repository.getWeatherCityByName(nameCity)))
        }
    }

    fun getResource(): StateFlow<Resource<WeatherCity>> = resource.asStateFlow()

    fun updateWeatherCity(isCurrentLocation: Boolean) {
        viewModelScope.launch {
            if (isCurrentLocation) {
                repository.updateWeatherCurrentLocation(resource.getData()!!.nameCity).collect {
                    dataPreparation(it)
                }
            } else {
                repository.updateWeatherCity(resource.getData()!!).collect {
                    resource.value = it
                }
            }
        }
    }

    private fun dataPreparation(resourceWeatherCity: Resource<WeatherCity>) {
        resource.value = Resource(EventStatus.CITY_WEATHER_DATA_UPDATED, resourceWeatherCity.getData())
    }
}