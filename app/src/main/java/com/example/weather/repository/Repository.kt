package com.example.weather.repository

import com.example.weather.repository.api.WeatherData
import com.example.weather.repository.dao.OrmLiteHelper
import com.example.weather.model.WeatherCity
import com.example.weather.utils.resource.event.EventStatus
import com.example.weather.utils.extensions.*
import com.example.weather.utils.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class Repository(private val dataBaseHelper: OrmLiteHelper,
                 private val weatherData: WeatherData) {

    fun getWeatherCities(): ArrayList<WeatherCity> =
        dataBaseHelper.getWeatherCities()

    fun getWeatherCityByName(nameCity: String): WeatherCity =
        dataBaseHelper.getWeatherCityByName(nameCity)

    fun createWeatherCity(nameCity: String, isCurrentLocation: Boolean)
        : Flow<Resource<WeatherCity>> = flow {

            val newWeatherCity = withContext(Dispatchers.IO) {
                weatherData.getWeatherCity(nameCity)
            }
            newWeatherCity.isCurrentLocation = isCurrentLocation

            if (isCurrentLocation) {
                emit(Resource(EventStatus.CURRENT_LOCATION_UPDATED,
                    dataBaseHelper.createWeatherCity(newWeatherCity)))
            } else {
                emit(Resource(EventStatus.CITY_ADDED,
                    dataBaseHelper.createWeatherCity(newWeatherCity)))
            }
    }.createWeatherException(nameCity)

    fun updateWeatherCity(weatherCity: WeatherCity): Flow<Resource<WeatherCity>> = flow {
            val newWeatherCity = withContext(Dispatchers.IO) {
                weatherData.getUpdateWeatherCity(weatherCity)
            }

            dataBaseHelper.updateWeatherCity(newWeatherCity)
            emit(Resource(EventStatus.CITY_WEATHER_DATA_UPDATED,
                dataBaseHelper.getWeatherCityByName(weatherCity.nameCity)))
    }.updateWeatherException(weatherCity)

    fun updateWeatherCities(): Flow<Resource<ArrayList<WeatherCity>>> = flow {
            val weatherCityList = withContext(Dispatchers.IO) {
                weatherData.getUpdatedWeatherCityList(dataBaseHelper.getWeatherCities())
            }

            if (weatherCityList.isNotEmpty()) {
                emit(Resource(EventStatus.CITY_WEATHER_DATA_UPDATED,
                    dataBaseHelper.updateRecyclerCitiesWeather(weatherCityList)))

            } else emit(Resource(EventStatus.IS_NOT_REFRESHING,
                dataBaseHelper.getWeatherCities()))
    }.updateWeatherException(dataBaseHelper.getWeatherCities())

    fun deletedWeatherCity(weatherCity: WeatherCity) {
        dataBaseHelper.deletedWeatherCity(weatherCity)
    }

    /**
     * Current Location
     * */
    fun getCurrentLocationWeather(): WeatherCity? =
        dataBaseHelper.getCurrentLocationWeather()

    fun updateWeatherCurrentLocation(newNameCurrentLocation: String)
        : Flow<Resource<WeatherCity>> = flow {

            val weatherCurrentLocation = getCurrentLocationWeather()
            lateinit var newWeatherCity: WeatherCity

            if (weatherCurrentLocation!!.nameCity == newNameCurrentLocation) {
                newWeatherCity = withContext(Dispatchers.IO) {
                    weatherData.getUpdateWeatherCity(weatherCurrentLocation)
                }

                emit(Resource(EventStatus.CURRENT_LOCATION_UPDATED,
                    dataBaseHelper.updateWeatherCity(newWeatherCity)))
            } else {
                newWeatherCity = withContext(Dispatchers.IO) {
                    weatherData.getWeatherCity(newNameCurrentLocation)
                }
                newWeatherCity.isCurrentLocation = true

                dataBaseHelper.deletedWeatherCity(weatherCurrentLocation)
                emit(Resource(EventStatus.CURRENT_LOCATION_UPDATED,
                    dataBaseHelper.createWeatherCity(newWeatherCity)))
            }
    }.updateWeatherException(dataBaseHelper.getCurrentLocationWeather())
}