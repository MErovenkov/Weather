package com.example.weather.api

import android.util.Log
import com.example.weather.dto.WeatherCurrentDto
import com.example.weather.dto.WeatherFutureDto
import com.example.weather.api.interfaces.IWeatherApi
import retrofit2.Call
import java.net.SocketTimeoutException
import javax.net.ssl.SSLException

class WeatherApiRequester(private val weatherApiService: IWeatherApi,
                          private val apiKayWeatherBit: String) {

    fun getWeatherCurrentDto(nameCity: String): WeatherCurrentDto {
        try {
            return weatherApiService
                .getWeatherCurrent(nameCity, apiKayWeatherBit).getResponseBody()
        } catch (e: NullPointerException) {
            Log.w("$e nameCity: $nameCity", e.stackTraceToString())
            throw NullPointerException()
        } catch (e: SocketTimeoutException) {
            Log.w(e.toString(), e.stackTraceToString())
            throw SSLException("SocketTimeoutException")
        }
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