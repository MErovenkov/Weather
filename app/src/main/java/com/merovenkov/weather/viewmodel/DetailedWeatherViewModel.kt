package com.merovenkov.weather.viewmodel

import androidx.lifecycle.*
import com.merovenkov.weather.data.repository.Repository
import com.merovenkov.weather.data.model.WeatherCity
import com.merovenkov.weather.utils.resource.Resource
import com.merovenkov.weather.utils.extensions.getData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailedWeatherViewModel(private val repository: Repository): ViewModel() {

    private var resource: MutableStateFlow<Resource<WeatherCity>> = MutableStateFlow(Resource(null))

    fun initResource(nameCity: String, isCurrentLocation: Boolean) {
        resource = if (isCurrentLocation) {
            MutableStateFlow(Resource(repository.getCurrentLocationWeather()))
        } else {
            MutableStateFlow(Resource(repository.getWeatherCityByName(nameCity)))
        }
    }

    fun initResourceByDeepLinkData(nameCity: String) {
        viewModelScope.launch {
            repository.getWeatherCityByDeepLinkData(nameCity).collect {
                resource.value = it
            }
        }
    }

    fun getResource(): StateFlow<Resource<WeatherCity>> = resource.asStateFlow()

    fun updateWeatherCity() {
        viewModelScope.launch {
            repository.updateWeatherCity(resource.getData()!!).collect {
                resource.value = it
            }
        }
    }
}