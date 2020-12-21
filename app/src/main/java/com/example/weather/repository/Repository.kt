package com.example.weather.repository

import com.example.weather.repository.api.WeatherData
import com.example.weather.repository.dao.OrmLiteHelper
import com.example.weather.model.WeatherCity
import com.example.weather.utils.*
import com.example.weather.utils.extensions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class Repository(private val dataBaseHelper: OrmLiteHelper,
                 private val weatherData: WeatherData) {

    fun getWeatherCities(): ArrayList<WeatherCity> =
        dataBaseHelper.getWeatherCities()

    fun getWeatherCityByName(nameCity: String): WeatherCity =
        dataBaseHelper.getWeatherCityByName(nameCity)

    fun createWeatherCity(nameCity: String) = flow {
            val newWeatherCity = withContext(Dispatchers.IO) {
                weatherData.getWeatherCity(nameCity)
            }

            dataBaseHelper.createWeatherCity(newWeatherCity)
            emit(Resource(EventStatus.CITY_ADDED, dataBaseHelper.getWeatherCities()))
    }.createWeatherException(nameCity)

    fun updateWeatherCity(weatherCity: WeatherCity) = flow {
            val newWeatherCity = withContext(Dispatchers.IO) {
                weatherData.getUpdateWeatherCity(weatherCity)
            }

            dataBaseHelper.updateWeatherCity(newWeatherCity)
            emit(Resource(EventStatus.CITY_WEATHER_DATA_UPDATED,
                dataBaseHelper.getWeatherCityByName(weatherCity.nameCity)))
    }.connectException()
     .sslUpdateException()

    fun updateAllCitiesWeather() = flow {
            val weatherCityList = withContext(Dispatchers.IO) {
                weatherData.getUpdatedWeatherCityList(dataBaseHelper.getWeatherCities())
            }

            if (weatherCityList.isNotEmpty()) {
                dataBaseHelper.updateAllCitiesWeather(weatherCityList)
                emit(Resource(EventStatus.CITY_WEATHER_DATA_UPDATED, dataBaseHelper.getWeatherCities()))
            } else emit(Resource(null, dataBaseHelper.getWeatherCities()))
    }.connectException()
     .sslUpdateException()
     .concurrentModificationException()

    fun deletedWeatherCity(weatherCity: WeatherCity) {
        dataBaseHelper.deletedWeatherCity(weatherCity)
    }

    /**
     * Current Location
     * */
    fun getCurrentLocationWeather(): WeatherCity? =
        dataBaseHelper.getCurrentLocationWeather()

    fun createWeatherCurrentLocation(nameCity: String) = flow {
            val weatherCurrentLocation = getCurrentLocationWeather()

            if (weatherCurrentLocation != null && weatherCurrentLocation.nameCity == nameCity) {
                val newWeatherCity = withContext(Dispatchers.IO) {
                    weatherData.getUpdateWeatherCity(weatherCurrentLocation)
                }

                dataBaseHelper.updateWeatherCity(newWeatherCity)
            } else if (weatherCurrentLocation != null && weatherCurrentLocation.nameCity != nameCity) {
                val newWeatherCity = withContext(Dispatchers.IO) {
                    weatherData.getWeatherCity(nameCity)
                }

                newWeatherCity.isCurrentLocation = true

                dataBaseHelper.deletedWeatherCity(weatherCurrentLocation)
                dataBaseHelper.createWeatherCity(newWeatherCity)
            } else {
                val newWeatherCity = withContext(Dispatchers.IO) {
                    weatherData.getWeatherCity(nameCity)
                }

                newWeatherCity.isCurrentLocation = true
                dataBaseHelper.createWeatherCity(newWeatherCity)
            }

            emit(Resource(dataBaseHelper.getWeatherCities()))
    }.connectException()

    fun updateWeatherCurrentLocation() = flow {
            val newWeatherCity = withContext(Dispatchers.IO) {
                weatherData.getUpdateWeatherCity(getCurrentLocationWeather()!!)
            }

            dataBaseHelper.updateWeatherCity(newWeatherCity)

            emit(Resource(EventStatus.CITY_WEATHER_DATA_UPDATED,
                dataBaseHelper.getCurrentLocationWeather()))
    }.connectException()
     .sslUpdateException()
     .concurrentModificationException()
}