package com.example.weather.utils.api

import android.util.Log
import com.example.weather.dto.WeatherCurrentDto
import com.example.weather.dto.WeatherFutureDto
import retrofit2.Call

class WeatherApiRequester(private val weatherApiService: NetworkService,
                          private val apiKayWeatherBit: String) {

    fun getWeatherCurrentDto(nameCity: String): WeatherCurrentDto {
        try {
            return weatherApiService.getWeatherApi()
                .getWeatherCurrent(nameCity, apiKayWeatherBit).getResponseBody()
        } catch (e: NullPointerException) {
            Log.w("$e nameCity: $nameCity", Thread.currentThread().stackTrace[2].toString())
            throw NullPointerException()
        }
    }

    fun getWeatherFutureDto(coordinateCityLat: String, coordinateCityLon: String)
            : WeatherFutureDto {

        return weatherApiService.getWeatherApi()
            .getWeatherFuture(coordinateCityLat, coordinateCityLon, apiKayWeatherBit)
            .getResponseBody()
    }

    private fun <T> Call<T>.getResponseBody(): T {
        return execute().body()!!
    }
}