package com.merovenkov.weather.data.repository.api

import android.content.res.Resources.NotFoundException
import android.graphics.*
import com.merovenkov.weather.data.dto.WeatherCurrentDto
import com.merovenkov.weather.data.dto.WeatherFutureDto
import com.merovenkov.weather.data.repository.api.interfaces.IWeatherApi
import com.merovenkov.weather.utils.ApiKeyChanger
import com.merovenkov.weather.utils.RequestData
import com.merovenkov.weather.utils.exception.OverLimitApiKeyException
import okhttp3.ResponseBody
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

class WeatherApiRequester(private val weatherApiService: IWeatherApi,
                          private val apiKeyChanger: ApiKeyChanger) {

    companion object {
        private const val CODE_RESPONSE_KEY_OVER_LIMIT = 429
    }

    @Throws(NotFoundException::class, OverLimitApiKeyException::class, ConnectException::class)
    fun getWeatherCurrentDto(nameCity: String): WeatherCurrentDto {
        return getResponseBody(RequestData(nameCity))
    }

    @Throws(NotFoundException::class, OverLimitApiKeyException::class, ConnectException::class)
    fun getWeatherCurrentDtoByCoordinate(coordinateCityLat: String, coordinateCityLon: String)
            : WeatherCurrentDto {
        return getResponseBody(RequestData(coordinateCityLat, coordinateCityLon, true))
    }

    @Throws(NotFoundException::class, OverLimitApiKeyException::class, ConnectException::class)
    fun getWeatherFutureDto(coordinateCityLat: String, coordinateCityLon: String)
            : WeatherFutureDto {
        return getResponseBody(RequestData(coordinateCityLat, coordinateCityLon, false))
    }

    @Throws(NotFoundException::class, OverLimitApiKeyException::class,
            ConnectException::class, SocketTimeoutException::class)
    fun getTileBitmap(layer: String, zoom: Int, x: Int, y: Int): Bitmap {
        val responseBody = getResponseBody(RequestData(true, layer, zoom, x, y)) as ResponseBody
        return responseBody.byteStream().use { BitmapFactory.decodeStream(it)}
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

                response.body() ?: throw NotFoundException()
            }

            else -> response.body() ?: throw NotFoundException()
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

            requestData.isTileUrl -> {
                weatherApiService.
                    getPrecipitationBitmap(requestData.layer!!,
                                          requestData.zoom!!,
                                          requestData.x!!,
                                          requestData.y!!,
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