package com.example.weather.repository.api

import android.util.Log
import com.example.weather.model.WeatherCity
import com.example.weather.utils.MapperWeatherData
import com.example.weather.utils.exception.NotFoundLocationException
import com.example.weather.utils.exception.OverLimitApiKeyException
import java.net.ConnectException
import kotlin.collections.ArrayList

class WeatherData(private val weatherApiRequester: WeatherApiRequester,
                  private val mapperWeatherData: MapperWeatherData) {

    private val tag = this.javaClass.simpleName

    fun getWeatherCity(nameCity: String): WeatherCity {
       try {
           val weatherCurrentDto = weatherApiRequester.getWeatherCurrentDto(nameCity)
           val weatherFutureDto = weatherApiRequester.getWeatherFutureDto(
                weatherCurrentDto.coordinatesCity.lat, weatherCurrentDto.coordinatesCity.lon)

            return mapperWeatherData.getWeatherCity(weatherCurrentDto, weatherFutureDto)
       } catch (e: Exception) {
           Log.w(tag,  e.stackTraceToString())
           when(e) {
               is NotFoundLocationException -> throw NotFoundLocationException("City not found: $nameCity")
               else -> throw definingException(e)
           }
       }
    }

    fun getWeatherCityByCoordinate(coordinateLat: Double, coordinateLon: Double): WeatherCity {
        try {
            val weatherCurrentDto = weatherApiRequester
                .getWeatherCurrentDtoByCoordinate(coordinateLat.toString(), coordinateLon.toString())
            val weatherFutureDto = weatherApiRequester.getWeatherFutureDto(
                weatherCurrentDto.coordinatesCity.lat, weatherCurrentDto.coordinatesCity.lon)

            return mapperWeatherData.getWeatherCity(weatherCurrentDto, weatherFutureDto)
        } catch (e: Exception) {
            Log.w(tag,  e.stackTraceToString())
            throw definingException(e)
        }
    }

    /**
     * Update WeatherCity
     * */
    fun getUpdateWeatherCity(oldWeatherCity: WeatherCity): WeatherCity {
        try {
            return broadcastingImmutableData(
                oldWeatherCity,
                getWeatherCity(oldWeatherCity.nameCity)
            )
        } catch (e: Exception) {
            Log.w(tag,  e.stackTraceToString())
            throw definingException(e)
        }
    }

    fun getUpdatedWeatherCityList(oldWeatherCityList: ArrayList<WeatherCity>): ArrayList<WeatherCity>{
        try {
            val newWeatherCityList: ArrayList<WeatherCity> = ArrayList()

            for (oldWeatherCity in oldWeatherCityList) {
                val newWeatherCity = getWeatherCity(oldWeatherCity.nameCity)
                newWeatherCityList.add(broadcastingImmutableData(oldWeatherCity, newWeatherCity))
            }
            return newWeatherCityList
        } catch (e: Exception) {
            Log.w(tag,  e.stackTraceToString())
            throw definingException(e)
        }
    }

    private fun definingException(e: Exception): Exception {
        return when(e) {
            is ConnectException -> ConnectException()
            is OverLimitApiKeyException -> OverLimitApiKeyException()
            else -> Exception()
        }
    }

    private fun broadcastingImmutableData(oldWeatherCity: WeatherCity, newWeatherCity: WeatherCity)
            : WeatherCity {
        newWeatherCity.id = oldWeatherCity.id
        newWeatherCity.isCurrentLocation = oldWeatherCity.isCurrentLocation
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