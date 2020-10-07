package com.example.weather.utils.api

import com.example.weather.dto.WeatherCurrentDto
import com.example.weather.dto.WeatherFutureDto

class WeatherApiRequester(private val apiKayWeatherBit: String) {
    private val weatherApiService = NetworkService()

    fun getWeatherCurrentDto(nameCity: String): WeatherCurrentDto {
        return weatherApiService.getWeatherApi()
            .getWeatherCurrent(nameCity, apiKayWeatherBit).execute().body()!!
    }

    fun getWeatherFutureDto(coordinateCityLat: String, coordinateCityLon: String)
            : WeatherFutureDto {
        return weatherApiService.getWeatherApi()
            .getWeatherFuture(coordinateCityLat, coordinateCityLon, apiKayWeatherBit)
            .execute().body()!!
    }
}