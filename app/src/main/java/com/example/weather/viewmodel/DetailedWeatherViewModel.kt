package com.example.weather.viewmodel

import androidx.lifecycle.*
import com.example.weather.repository.Repository
import com.example.weather.model.WeatherCity
import com.example.weather.utils.Resource
import com.example.weather.utils.getData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailedWeatherViewModel(private val repository: Repository): ViewModel() {

    private lateinit var resource: MutableStateFlow<Resource<WeatherCity>>

    fun initLiveData(nameCity: String) {
        resource = MutableStateFlow(Resource(repository.getWeatherCityByName(nameCity).value))
    }

    fun getResource(): StateFlow<Resource<WeatherCity>> = resource.asStateFlow()

    fun updateWeatherData() {
        viewModelScope.launch {
            repository.updateWeatherCity(resource.getData()!!).collect {
                resource.value = it
            }
        }
    }
}