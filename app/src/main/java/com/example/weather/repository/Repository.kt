package com.example.weather.repository

import android.util.Log
import com.example.weather.repository.api.WeatherData
import com.example.weather.repository.dao.OrmLiteHelper
import com.example.weather.model.WeatherCity
import com.example.weather.utils.EventStatus
import com.example.weather.utils.Resource
import com.example.weather.utils.getWeatherCities
import com.example.weather.utils.getWeatherCityByName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.sql.SQLException
import javax.net.ssl.SSLException

class Repository(private val dataBaseHelper: OrmLiteHelper,
                 private val weatherData: WeatherData) {

    fun getWeatherCities(): StateFlow<ArrayList<WeatherCity>> = MutableStateFlow(
        dataBaseHelper.getWeatherCities())

    fun getWeatherCityByName(nameCity: String): StateFlow<WeatherCity> = MutableStateFlow(
        dataBaseHelper.getWeatherCityByName(nameCity))

    fun createWeatherCity(nameCity: String) = flow {
            val newWeatherCity = withContext(Dispatchers.IO) {
                    weatherData.getWeatherCity(nameCity)
            }
            dataBaseHelper.createWeatherCity(newWeatherCity)
            emit(Resource(EventStatus.CITY_ADDED, dataBaseHelper.getWeatherCities()))
        }.catch { e ->
            when(e) {
                is NullPointerException -> {
                    Log.w("$e nameCity: $nameCity", e.stackTraceToString())
                    emit(Resource(EventStatus.CITY_NOT_FOUND, null))
                }

                is SQLException -> {
                    Log.w("$e nameCity: $nameCity", e.stackTraceToString())
                    emit(Resource(EventStatus.CITY_EXIST))
                }
            }
    }

    fun updateWeatherCity(weatherCity: WeatherCity) = flow {
            val newWeatherCity = withContext(Dispatchers.IO) {
                weatherData.getUpdateWeatherCity(weatherCity)
            }
            dataBaseHelper.updateWeatherCity(newWeatherCity)
            emit(Resource(EventStatus.CITY_WEATHER_DATA_UPDATED,
                dataBaseHelper.getWeatherCityByName(weatherCity.nameCity)))
        }.catch { e ->
            when(e) {
                is ConnectException -> {
                    Log.e(e.toString(), e.stackTraceToString())
                    emit(Resource(EventStatus.LOST_INTERNET_ACCESS))
                }

                is SSLException -> {
                    Log.w(e.toString(), e.stackTraceToString())
                    emit(Resource(EventStatus.CITY_WEATHER_UPDATE_FAILED))
                }
            }
    }

    fun updateAllCitiesWeather() = flow {
            val weatherCityList = withContext(Dispatchers.IO) {
                weatherData.getUpdatedWeatherCityList(dataBaseHelper.getWeatherCities())
            }
            if (weatherCityList.isNotEmpty()) {
                dataBaseHelper.updateAllCitiesWeather(weatherCityList)
                emit(Resource(EventStatus.CITY_WEATHER_DATA_UPDATED, dataBaseHelper.getWeatherCities()))
            }
        }.catch { e ->
            when (e) {
                is ConcurrentModificationException -> {
                    Log.e(e.toString(), e.stackTraceToString())
                    emit(Resource(EventStatus.CITY_WEATHER_UPDATE_FAILED))
                }

                is ConnectException -> {
                    Log.w(e.toString(), e.stackTraceToString())
                    emit(Resource(EventStatus.LOST_INTERNET_ACCESS))
                }

                is SSLException -> {
                    Log.w(e.toString(), e.stackTraceToString())
                    emit(Resource(EventStatus.CITY_WEATHER_UPDATE_FAILED))
                }
            }
    }

    fun deletedWeatherCity(weatherCity: WeatherCity) {
        dataBaseHelper.deletedWeatherCity(weatherCity)
    }
}