package com.example.weather.data.repository.api

import com.example.weather.data.model.WeatherCity
import com.example.weather.utils.MapperWeatherData
import com.example.weather.utils.exception.NotFoundLocationException
import com.example.weather.utils.exception.OverLimitApiKeyException
import java.net.ConnectException
import kotlin.collections.ArrayList
import kotlin.jvm.Throws

class WeatherData(private val weatherApiRequester: WeatherApiRequester,
                  private val mapperWeatherData: MapperWeatherData) {

    @Throws(NotFoundLocationException::class, OverLimitApiKeyException::class, ConnectException::class)
    fun getWeatherCity(nameCity: String): WeatherCity {
       val weatherCurrentDto = weatherApiRequester.getWeatherCurrentDto(nameCity)
       val weatherFutureDto = weatherApiRequester.getWeatherFutureDto(
            weatherCurrentDto.coordinatesCity.lat, weatherCurrentDto.coordinatesCity.lon)

        return mapperWeatherData.getWeatherCity(weatherCurrentDto, weatherFutureDto)
    }

    @Throws(OverLimitApiKeyException::class, ConnectException::class)
    fun getWeatherCityByCoordinate(coordinateLat: Double, coordinateLon: Double): WeatherCity {
        val weatherCurrentDto = weatherApiRequester
            .getWeatherCurrentDtoByCoordinate(coordinateLat.toString(), coordinateLon.toString())
        val weatherFutureDto = weatherApiRequester.getWeatherFutureDto(
            weatherCurrentDto.coordinatesCity.lat, weatherCurrentDto.coordinatesCity.lon)

        return mapperWeatherData.getWeatherCity(weatherCurrentDto, weatherFutureDto)
    }

    /**
     * Update WeatherCity
     * */
    @Throws(OverLimitApiKeyException::class, ConnectException::class)
    fun getUpdateWeatherCity(oldWeatherCity: WeatherCity): WeatherCity {
        return broadcastingImmutableData(
            oldWeatherCity,
            getWeatherCity(oldWeatherCity.nameCity)
        )
    }

    @Throws(OverLimitApiKeyException::class, ConnectException::class)
    fun getUpdatedWeatherCityList(oldWeatherCityList: ArrayList<WeatherCity>): ArrayList<WeatherCity>{
        val newWeatherCityList: ArrayList<WeatherCity> = ArrayList()

        for (oldWeatherCity in oldWeatherCityList) {
            val newWeatherCity = getWeatherCity(oldWeatherCity.nameCity)
            newWeatherCityList.add(broadcastingImmutableData(oldWeatherCity, newWeatherCity))
        }
        return newWeatherCityList
    }

    fun broadcastingImmutableData(oldWeatherCity: WeatherCity, newWeatherCity: WeatherCity)
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