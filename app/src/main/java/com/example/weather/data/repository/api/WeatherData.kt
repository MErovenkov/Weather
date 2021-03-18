package com.example.weather.data.repository.api

import android.content.res.Resources.NotFoundException
import com.example.weather.utils.resource.TileData
import com.example.weather.data.model.WeatherCity
import com.example.weather.utils.MapperWeatherData
import com.example.weather.utils.exception.OverLimitApiKeyException
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.collections.ArrayList
import kotlin.jvm.Throws

class WeatherData(private val weatherApiRequester: WeatherApiRequester,
                  private val mapperWeatherData: MapperWeatherData) {

    @Throws(NotFoundException::class, OverLimitApiKeyException::class, ConnectException::class)
    fun getWeatherCity(nameCity: String): WeatherCity {
       val weatherCurrentDto = weatherApiRequester.getWeatherCurrentDto(nameCity)
       val weatherFutureDto = weatherApiRequester.getWeatherFutureDto(
            weatherCurrentDto.coordinatesCity.lat, weatherCurrentDto.coordinatesCity.lon)

        return mapperWeatherData.getWeatherCity(weatherCurrentDto, weatherFutureDto)
    }

    @Throws(NotFoundException::class, OverLimitApiKeyException::class, ConnectException::class)
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

    @Throws(NotFoundException::class, OverLimitApiKeyException::class,
            ConnectException::class, SocketTimeoutException::class)
    fun getTileData(layer: String, zoom: Int, x: Int, y: Int): TileData {
        return TileData(weatherApiRequester.getTileBitmap(layer, zoom, x, y), zoom, x, y)
    }
}