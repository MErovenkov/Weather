package com.example.weather.repository.api

import android.util.Log
import com.example.weather.model.WeatherCity
import com.example.weather.utils.MapperWeatherData
import java.net.ConnectException
import kotlin.collections.ArrayList

class WeatherData(private val weatherApiRequester: WeatherApiRequester,
                  private val mapperWeatherData: MapperWeatherData) {

    fun getWeatherCity(nameCity: String): WeatherCity {
       try {
           val weatherCurrentDto = weatherApiRequester.getWeatherCurrentDto(nameCity)
           val weatherFutureDto = weatherApiRequester.getWeatherFutureDto(
                weatherCurrentDto.coordinatesCity.lat, weatherCurrentDto.coordinatesCity.lon)

           return mapperWeatherData.getWeatherCity(weatherCurrentDto, weatherFutureDto)
       } catch (e: NullPointerException) {
            Log.w("$e nameCity: $nameCity",  e.stackTraceToString())
            throw NullPointerException()
       } catch (e: ConnectException) {
           Log.w(e.toString(),  e.stackTraceToString())
           throw ConnectException()
       }
    }

    fun getUpdateWeatherCity(oldWeatherCity: WeatherCity): WeatherCity {
        return changingId(oldWeatherCity, getWeatherCity(oldWeatherCity.nameCity))
    }

    fun getUpdatedWeatherCityList(oldWeatherCityList: ArrayList<WeatherCity>): ArrayList<WeatherCity>{
        try {
            val newWeatherCityList: ArrayList<WeatherCity> = ArrayList()

            for (oldWeatherCity in oldWeatherCityList) {
                val newWeatherCity = getWeatherCity(oldWeatherCity.nameCity)
                newWeatherCityList.add(changingId(oldWeatherCity, newWeatherCity))
            }
            return newWeatherCityList
        } catch (e: ConnectException) {
            Log.w(e.toString(),  e.stackTraceToString())
            throw ConnectException()
        }
    }

    private fun changingId(oldWeatherCity: WeatherCity, newWeatherCity: WeatherCity)
            : WeatherCity {
        newWeatherCity.id = oldWeatherCity.id
        newWeatherCity.weatherCurrent.id = oldWeatherCity.weatherCurrent.id

        val newWeatherFutureList = ArrayList(newWeatherCity.weatherFutureList)
        val oldWeatherFutureList = ArrayList(oldWeatherCity.weatherFutureList)
        for (newWeatherFuture in newWeatherFutureList.indices) {
            for (oldWeatherFuture in oldWeatherCity.weatherFutureList.indices) {
                if (newWeatherFuture == oldWeatherFuture) {
                    newWeatherFutureList[newWeatherFuture].id =
                        oldWeatherFutureList[oldWeatherFuture].id
                }
            }
        }

        return newWeatherCity
    }
}

