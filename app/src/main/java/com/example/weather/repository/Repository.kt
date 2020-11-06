package com.example.weather.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.weather.repository.api.WeatherData
import com.example.weather.repository.dao.OrmLiteHelper
import com.example.weather.model.WeatherCity
import com.example.weather.utils.Event
import com.example.weather.utils.EventStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.sql.SQLException
import javax.net.ssl.SSLException

class Repository(private val dataBaseHelper: OrmLiteHelper,
                 private val weatherData: WeatherData) {

    private var eventStatus = MutableLiveData<Event<Int>>()
    private var weatherCities : MutableLiveData<ArrayList<WeatherCity>> = MutableLiveData()

    init {
        updateWeatherCityMutableLiveData()
    }

    private fun updateWeatherCityMutableLiveData() {
        GlobalScope.launch(Dispatchers.Main) {
            weatherCities.value =
                dataBaseHelper.getWeatherCityDao().queryForAll() as ArrayList<WeatherCity>?
        }
    }

    fun getWeatherCities() = weatherCities
    fun getEventStatus() = eventStatus

    fun createWeatherCity(nameCity: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val newWeatherCity = withContext(Dispatchers.IO) {
                        weatherData.getWeatherCity(nameCity)
                }
                dataBaseHelper.createWeatherCity(newWeatherCity)
                updateWeatherCityMutableLiveData()
                eventStatus.value = Event(EventStatus.CITY_ADDED)
            } catch (e: NullPointerException) {
                eventStatus.value = Event(EventStatus.CITY_NOT_FOUND)
                Log.w("$e nameCity: $nameCity", e.stackTraceToString())
            } catch (e: SQLException) {
                eventStatus.value = Event(EventStatus.CITY_EXIST)
                Log.w("$e nameCity: $nameCity", e.stackTraceToString())
            }
        }
    }

    fun updateWeatherCity(weatherCity: WeatherCity) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val newWeatherCity = withContext(Dispatchers.IO) {
                    weatherData.getUpdateWeatherCity(weatherCity)
                }
                dataBaseHelper.updateWeatherCity(newWeatherCity)
                updateAllCitiesWeather()
                eventStatus.value = Event(EventStatus.CITY_WEATHER_DATA_UPDATED)
            } catch (e: ConnectException) {
                eventStatus.value = Event(EventStatus.LOST_INTERNET_ACCESS)
                Log.e(e.toString(), e.stackTraceToString())
            } catch (e: SSLException) {
                eventStatus.value = Event(EventStatus.CITY_WEATHER_UPDATE_FAILED)
                Log.w(e.toString(), e.stackTraceToString())
            }
        }
    }

    fun updateAllCitiesWeather() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val weatherCityList = withContext(Dispatchers.IO) {
                    weatherData.getUpdatedWeatherCityList(weatherCities.value!!)
                }
                if (weatherCityList.isNotEmpty()) {
                    dataBaseHelper.updateAllCitiesWeather(weatherCityList)
                    updateWeatherCityMutableLiveData()
                    eventStatus.value = Event(EventStatus.CITY_WEATHER_DATA_UPDATED)
                }
            } catch (e: ConcurrentModificationException) {
                eventStatus.value = Event(EventStatus.CITY_WEATHER_UPDATE_FAILED)
                Log.w(e.toString(), e.stackTraceToString())
            } catch (e: ConnectException) {
                eventStatus.value = Event(EventStatus.LOST_INTERNET_ACCESS)
                Log.w(e.toString(), e.stackTraceToString())
            } catch (e: SSLException) {
                eventStatus.value = Event(EventStatus.CITY_WEATHER_UPDATE_FAILED)
                Log.w(e.toString(), e.stackTraceToString())
            }
        }
    }

    fun deletedWeatherCity(weatherCity: WeatherCity) {
        dataBaseHelper.deletedWeatherCity(weatherCity)
    }
}