package com.example.weather.utils.extensions

import android.util.Log
import android.content.res.Resources.NotFoundException
import com.example.weather.utils.exception.OverLimitApiKeyException
import com.example.weather.utils.resource.event.EventStatus
import com.example.weather.utils.resource.Resource
import io.reactivex.rxjava3.core.Single
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.sql.SQLException
import java.util.*
import javax.net.ssl.SSLException

private const val TAG = "utils.extension.Rx"

/**
 *  Exception in Single
 * */
@Suppress("UNCHECKED_CAST")
fun <T> Single<T>.exceptionCreateWeather(): Single<T> {
    return onErrorReturn { e ->
        Log.w(TAG, e.stackTraceToString())
        return@onErrorReturn when (e) {
            is NotFoundException -> Resource(EventStatus.CITY_NOT_FOUND, null) as T
            is SQLException -> Resource(EventStatus.CITY_EXIST, null) as T
            is ConnectException -> Resource(EventStatus.LOST_INTERNET_ACCESS, null) as T
            is OverLimitApiKeyException -> Resource(EventStatus.REQUEST_LIMIT_EXCEEDED, null) as T
            else -> throw e
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Single<T>.exceptionCreateWeatherLocation(): Single<T> {
    return onErrorReturn { e ->
        Log.w(TAG, e.stackTraceToString())
        return@onErrorReturn when (e) {
            is NotFoundException -> Resource(EventStatus.LOCATION_INFO_FAILURE, null) as T
            is ConnectException -> Resource(EventStatus.LOST_INTERNET_ACCESS, null) as T
            is OverLimitApiKeyException -> Resource(EventStatus.REQUEST_LIMIT_EXCEEDED, null) as T
            else -> throw e
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T1, T2> Single<T1>.exceptionUpdateWeather(weatherCityData: T2): Single<T1> {
    return onErrorReturn { e ->
        Log.w(TAG, e.stackTraceToString())
        return@onErrorReturn when (e) {
            is SSLException -> Resource(EventStatus.CITY_WEATHER_UPDATE_FAILED, weatherCityData) as T1
            is ConnectException -> Resource(EventStatus.LOST_INTERNET_ACCESS, weatherCityData) as T1
            is OverLimitApiKeyException -> Resource(EventStatus.REQUEST_LIMIT_EXCEEDED,
                weatherCityData) as T1
            else -> throw e
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Single<T>.exceptionGettingWeatherByDeepLink(): Single<T> {
    return onErrorReturn { e ->
        Log.w(TAG, e.stackTraceToString())
        return@onErrorReturn when (e) {
            is NotFoundException -> Resource(EventStatus.CITY_NOT_FOUND, null) as T
            is ConnectException -> Resource(EventStatus.LOST_INTERNET_ACCESS, null) as T
            is OverLimitApiKeyException -> Resource(EventStatus.REQUEST_LIMIT_EXCEEDED, null) as T
            else -> throw e
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Single<T>.exceptionGettingPrecipitation(): Single<T> {
    return onErrorReturn { e ->
        Log.w(TAG, e.stackTraceToString())
        return@onErrorReturn when (e) {
            is NotFoundException -> Resource(EventStatus.PRECIPITATION_TILE_FAILURE, null) as T
            is SocketTimeoutException -> Resource(EventStatus.LOST_INTERNET_ACCESS, null) as T
            is UnknownHostException -> Resource(EventStatus.LOST_INTERNET_ACCESS, null) as T
            is ConnectException -> Resource(EventStatus.LOST_INTERNET_ACCESS, null) as T
            is OverLimitApiKeyException -> Resource(EventStatus.REQUEST_LIMIT_EXCEEDED, null) as T
            else -> throw e
        }
    }
}