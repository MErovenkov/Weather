package com.example.weather.viewmodel

import androidx.lifecycle.*
import com.example.weather.data.repository.Repository
import com.example.weather.data.model.WeatherCity
import com.example.weather.utils.extensions.getData
import com.example.weather.utils.resource.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: Repository): ViewModel() {

    private var resourceRecycler: MutableStateFlow<Resource<ArrayList<WeatherCity>>>
            = MutableStateFlow(Resource(repository.getWeatherCities()))

    private var resourceLocation: MutableStateFlow<Resource<WeatherCity>>
            = MutableStateFlow(Resource(repository.getCurrentLocationWeather()))

    fun getResourceRecycler(): StateFlow<Resource<ArrayList<WeatherCity>>> = resourceRecycler.asStateFlow()

    fun createWeatherData(nameCity: String) {
        viewModelScope.launch {
            repository.createWeatherCity(nameCity).collect {
                addWeatherData(it)
            }
        }
    }

    private fun addWeatherData(resourceWeatherCity: Resource<WeatherCity>) {
        val tmp: ArrayList<WeatherCity> = ArrayList(resourceRecycler.getData()!!)

        resourceWeatherCity.getData()?.let { tmp.add(it)}
        resourceRecycler.value =
            resourceWeatherCity.getEvent()?.getStatusIfNotHandled()?.let { Resource(it, tmp) }!!
    }

    fun updateWeatherCities() {
        viewModelScope.launch {
            repository.updateWeatherCities().collect {
                resourceRecycler.value = it
            }
        }
    }

    fun deleteWeatherCity(weatherCity: WeatherCity) {
        repository.deletedWeatherCity(weatherCity)
    }

    /**
     *  Current Location
     * */
    fun getResourceLocation(): StateFlow<Resource<WeatherCity>> = resourceLocation.asStateFlow()

    fun createWeatherCurrentLocation(coordinateLat: Double, coordinateLon: Double) {
        viewModelScope.launch {
            repository.createWeatherCurrentLocation(coordinateLat, coordinateLon)
                .collect {

                    resourceLocation.value = it
                }
        }
    }

    fun createWeatherCurrentLocation(nameCity: String) {
        viewModelScope.launch {
            repository.createWeatherCurrentLocation(nameCity).collect {
                resourceLocation.value = it
            }
        }
    }
}