package com.example.weather.repository.api

import android.util.Log
import com.example.weather.dto.WeatherCurrentDto
import com.example.weather.dto.WeatherFutureDto
import com.example.weather.repository.api.interfaces.IWeatherApi
import retrofit2.Call

class WeatherApiRequester(private val weatherApiService: IWeatherApi,
                          private val apiKayWeatherBit: String) {

    fun getWeatherCurrentDto(nameCity: String): WeatherCurrentDto {
        try {
            return weatherApiService
                .getWeatherCurrent(nameCity, apiKayWeatherBit).getResponseBody()
        } catch (e: NullPointerException) {
            Log.w("City not found: $nameCity", e.stackTraceToString())
            throw NullPointerException()
        }
    }

    fun getWeatherCurrentDtoByCoordinate(coordinateCityLat: String, coordinateCityLon: String)
            : WeatherCurrentDto {

        return weatherApiService
            .getWeatherCurrentByCoordinate(coordinateCityLat, coordinateCityLon, apiKayWeatherBit)
            .getResponseBody()
    }

    fun getWeatherFutureDto(coordinateCityLat: String, coordinateCityLon: String)
            : WeatherFutureDto {

        return weatherApiService
            .getWeatherFuture(coordinateCityLat, coordinateCityLon, apiKayWeatherBit)
            .getResponseBody()
    }

    private fun <T> Call<T>.getResponseBody(): T {
        return execute().body()!!
    }
}