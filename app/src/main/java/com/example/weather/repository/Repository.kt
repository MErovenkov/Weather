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

    fun createWeatherCity(nameCity: String)
        : Flow<Resource<WeatherCity>> = flow {

            val newWeatherCity = withContext(Dispatchers.IO) {
                weatherData.getWeatherCity(nameCity)
            }

            emit(Resource(EventStatus.CITY_ADDED,
                dataBaseHelper.createWeatherCity(newWeatherCity)))
    }.exceptionCreateWeather()

    fun updateWeatherCity(weatherCity: WeatherCity): Flow<Resource<WeatherCity>> = flow {
            val newWeatherCity = withContext(Dispatchers.IO) {
                weatherData.getUpdateWeatherCity(weatherCity)
            }

            emit(Resource(EventStatus.CITY_WEATHER_DATA_UPDATED,
                dataBaseHelper.updateWeatherCity(newWeatherCity)))
    }.exceptionUpdateWeather(weatherCity)

    fun updateWeatherCities(): Flow<Resource<ArrayList<WeatherCity>>> = flow {
            val weatherCityList = withContext(Dispatchers.IO) {
                weatherData.getUpdatedWeatherCityList(dataBaseHelper.getWeatherCities())
            }

            if (weatherCityList.isNotEmpty()) {
                emit(Resource(EventStatus.CITY_WEATHER_DATA_UPDATED,
                    dataBaseHelper.updateRecyclerCitiesWeather(weatherCityList)))

            } else emit(Resource(EventStatus.IS_NOT_REFRESHING,
                dataBaseHelper.getWeatherCities()))
    }.exceptionUpdateWeather(dataBaseHelper.getWeatherCities())

    fun deletedWeatherCity(weatherCity: WeatherCity) {
        dataBaseHelper.deletedWeatherCity(weatherCity)
    }

    /**
     * Current Location
     * */
    fun getCurrentLocationWeather(): WeatherCity? =
        dataBaseHelper.getCurrentLocationWeather()

    fun createWeatherCurrentLocation(coordinateLat: Double, coordinateLon: Double)
            : Flow<Resource<WeatherCity>> = flow {

        val newWeatherCity: WeatherCity = withContext(Dispatchers.IO) {
            weatherData.getWeatherCityByCoordinate(coordinateLat, coordinateLon)
        }

        emit(Resource(EventStatus.CURRENT_LOCATION_RECEIVED,
            changeWeatherCurrentLocation(newWeatherCity)))
    }.exceptionCreateWeatherLocation()


    fun createWeatherCurrentLocation(nameCity: String)
            : Flow<Resource<WeatherCity>> = flow {

        val newWeatherCity: WeatherCity = withContext(Dispatchers.IO) {
            weatherData.getWeatherCity(nameCity)
        }

        emit(Resource(EventStatus.CURRENT_LOCATION_RECEIVED,
            changeWeatherCurrentLocation(newWeatherCity)))
    }.exceptionCreateWeatherLocation()

    private fun changeWeatherCurrentLocation(newWeatherCity: WeatherCity): WeatherCity {
        val weatherCurrentLocation = getCurrentLocationWeather()

        if (weatherCurrentLocation != null) {
            dataBaseHelper.deletedWeatherCity(weatherCurrentLocation)
        }

        newWeatherCity.isCurrentLocation = true

        return dataBaseHelper.createWeatherCity(newWeatherCity)
    }
}