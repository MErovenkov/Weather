package com.example.weather.repository.api

import android.util.Log
import com.example.weather.dto.WeatherCurrentDto
import com.example.weather.dto.WeatherFutureDto
import com.example.weather.repository.api.interfaces.IWeatherApi
import com.example.weather.utils.ApiKeyChanger
import com.example.weather.utils.RequestData
import retrofit2.Response

class WeatherApiRequester(private val weatherApiService: IWeatherApi,
                          private val apiKeyChanger: ApiKeyChanger) {

    private val tag = this.javaClass.simpleName

    fun getWeatherCurrentDto(nameCity: String): WeatherCurrentDto {
        try {
            return getResponseBody(RequestData(nameCity))
        } catch (e: NullPointerException) {
            Log.w(tag, e.stackTraceToString())

            if (e.message == "Request limit exceeded") {
                throw NullPointerException("Request limit exceeded")
            } else {
                throw NullPointerException("City not found: $nameCity")
            }
        }
    }

    fun getWeatherCurrentDtoByCoordinate(coordinateCityLat: String, coordinateCityLon: String)
            : WeatherCurrentDto {
        try {
            return getResponseBody(RequestData(coordinateCityLat, coordinateCityLon, true))
        } catch (e: NullPointerException) {
            Log.w(tag, e.stackTraceToString())
            throw NullPointerException("Request limit exceeded")
        }
    }

    fun getWeatherFutureDto(coordinateCityLat: String, coordinateCityLon: String)
            : WeatherFutureDto {
        try {
            return getResponseBody(RequestData(coordinateCityLat, coordinateCityLon, false))
        } catch (e: NullPointerException) {
            Log.w(tag, e.stackTraceToString())
            throw NullPointerException("Request limit exceeded")
        }
    }

    private fun <T> getResponseBody(requestData: RequestData): T {
        var response = getResponseByRequestData<T>(requestData)

        return when (isOverLimit(response.code())) {
            true -> {
                while(apiKeyChanger.changeApiKey()) {
                    response = getResponseByRequestData(requestData)

                    if (!isOverLimit(response.code())) {
                        break
                    }
                }

                response.body()!!
            }

            else -> response.body()!!
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getResponseByRequestData(requestData: RequestData): Response<T> {
        return when {
            requestData.nameCity != null -> {
                weatherApiService
                    .getWeatherCurrent(requestData.nameCity, apiKeyChanger.getApiKey())
                    .execute() as Response<T>
            }

            requestData.isCurrent -> {
                weatherApiService
                    .getWeatherCurrentByCoordinate(requestData.coordinateLat!!,
                                                   requestData.coordinateLon!!,
                                                   apiKeyChanger.getApiKey())
                    .execute() as Response<T>
            }

            else -> {
                weatherApiService
                    .getWeatherFuture(requestData.coordinateLat!!, requestData.coordinateLon!!,
                                      apiKeyChanger.getApiKey())
                    .execute() as Response<T>
            }
        }
    }

    private fun isOverLimit(responseCode: Int): Boolean {
        return when(responseCode) {
            200 -> false
            404 -> throw NullPointerException()
            429 -> true
            else -> false
        }
    }
}