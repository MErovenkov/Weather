package com.example.weather.utils

import android.content.Context
import android.util.Log
import com.example.weather.R
import com.example.weather.model.WeatherCity
import com.example.weather.utils.api.WeatherApiRequester
import java.io.FileNotFoundException
import java.net.ConnectException
import kotlin.collections.ArrayList

class WeatherData(context: Context) {
    private val weatherApiRequester =
        WeatherApiRequester(context.getString(R.string.open_weather_map_api_key))
    private val mapperWeatherData = MapperWeatherData(context)

    fun getWeatherCity(nameCity: String): WeatherCity {
       try {
            val weatherCurrentDto = weatherApiRequester.getWeatherCurrentDto(nameCity)
            val weatherFutureDto = weatherApiRequester.getWeatherFutureDto(
                weatherCurrentDto.coordinatesCity.lat, weatherCurrentDto.coordinatesCity.lon)

           return mapperWeatherData.getWeatherCity(weatherCurrentDto, weatherFutureDto)
       } catch (e: FileNotFoundException) {
            Log.w("$e nameCity: $nameCity", Thread.currentThread().stackTrace[2].toString())
            throw FileNotFoundException()
       } catch (e: ConnectException) {
           Log.w(e.toString(), Thread.currentThread().stackTrace[2].toString())
           throw ConnectException()
       }
    }

    fun getUpdatedWeatherCityList(oldWeatherCityList: ArrayList<WeatherCity>): ArrayList<WeatherCity>{
        try {
            val newWeatherCityList: ArrayList<WeatherCity> = ArrayList()

            for (oldWeatherCity in oldWeatherCityList) {
                val newWeatherCity = getWeatherCity(oldWeatherCity.nameCity)

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
                newWeatherCityList.add(newWeatherCity)
            }
            return newWeatherCityList
        } catch (e: ConnectException) {
            Log.w(e.toString(), Thread.currentThread().stackTrace[2].toString())
            throw ConnectException()
        }
    }
}

