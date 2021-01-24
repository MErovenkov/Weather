package com.example.weather.repository.api

import com.example.weather.dto.WeatherCurrentDto
import com.example.weather.dto.WeatherFutureDto
import com.example.weather.repository.api.interfaces.IWeatherApi
import com.example.weather.utils.ApiKeyChanger
import com.example.weather.utils.RequestData
import com.example.weather.utils.exception.NotFoundLocationException
import com.example.weather.utils.exception.OverLimitApiKeyException
import retrofit2.Response
import java.net.ConnectException
import kotlin.jvm.Throws

class WeatherApiRequester(private val weatherApiService: IWeatherApi,
                          private val apiKeyChanger: ApiKeyChanger) {

    companion object {
        private const val CODE_RESPONSE_KEY_OVER_LIMIT = 429
    }

    @Throws(NotFoundLocationException::class, OverLimitApiKeyException::class, ConnectException::class)
    fun getWeatherCurrentDto(nameCity: String): WeatherCurrentDto {
        return getResponseBody(RequestData(nameCity))
    }

    @Throws(OverLimitApiKeyException::class, ConnectException::class)
    fun getWeatherCurrentDtoByCoordinate(coordinateCityLat: String, coordinateCityLon: String)
            : WeatherCurrentDto {
        return getResponseBody(RequestData(coordinateCityLat, coordinateCityLon, true))
    }

    @Throws(OverLimitApiKeyException::class, ConnectException::class)
    fun getWeatherFutureDto(coordinateCityLat: String, coordinateCityLon: String)
            : WeatherFutureDto {
        return getResponseBody(RequestData(coordinateCityLat, coordinateCityLon, false))
    }

    private fun <T> getResponseBody(requestData: RequestData): T {
        var response = getResponseByRequestData<T>(requestData)

        return when (response.code() == CODE_RESPONSE_KEY_OVER_LIMIT) {
            true -> {
                while(apiKeyChanger.changeApiKey()) {
                    response = getResponseByRequestData(requestData)

                    if (response.code() != CODE_RESPONSE_KEY_OVER_LIMIT) {
                        break
                    }
                }

                response.body() ?: throw NotFoundLocationException()
            }

            else -> response.body() ?: throw NotFoundLocationException()
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
}