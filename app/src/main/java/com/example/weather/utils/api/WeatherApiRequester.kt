package com.example.weather.utils.api

import android.util.Log
import com.example.weather.dto.WeatherCurrentDto
import com.example.weather.dto.WeatherFutureDto
import java.io.FileNotFoundException

class WeatherApiRequester(private val apiKayWeatherBit: String) {
    private val weatherApiService = NetworkService()

    fun getWeatherCurrentDto(nameCity: String): WeatherCurrentDto {
        try {
            return weatherApiService.getWeatherApi()
                .getWeatherCurrent(nameCity, apiKayWeatherBit).execute().body()!!
        } catch (e: NullPointerException) {
            Log.w("$e nameCity: $nameCity", Thread.currentThread().stackTrace[2].toString())
            throw NullPointerException()
        }
    }

    fun getWeatherFutureDto(coordinateCityLat: String, coordinateCityLon: String)
            : WeatherFutureDto {
        return weatherApiService.getWeatherApi()
            .getWeatherFuture(coordinateCityLat, coordinateCityLon, apiKayWeatherBit)
            .execute().body()!!
    }
}