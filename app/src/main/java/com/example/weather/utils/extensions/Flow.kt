package com.example.weather.utils.extensions

import android.util.Log
import com.example.weather.utils.resource.event.EventStatus
import com.example.weather.utils.resource.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import java.net.ConnectException
import java.sql.SQLException
import javax.net.ssl.SSLException

private const val TAG = "utils.extension.Flow"

/**
 * StateFlow extension
 */
fun <T> MutableStateFlow<Resource<T>>.getData(): T? {
    return value.getData()
}

/**
 *  Exception in flow
 * */
@Suppress("UNCHECKED_CAST")
fun <T> Flow<T>.exceptionCreateWeather(): Flow<T> {
    return catch { e ->
        when (e) {
            is NullPointerException -> {
                Log.w(TAG, e.stackTraceToString())
                if (e.message == "Request limit exceeded") {
                    emit(Resource(EventStatus.REQUEST_LIMIT_EXCEEDED, null) as T )
                } else {
                    emit(Resource(EventStatus.CITY_NOT_FOUND, null) as T )
                }
            }

            is SQLException -> {
                Log.w(TAG, e.stackTraceToString())
                emit(Resource(EventStatus.CITY_EXIST, null) as T)
            }

            is ConnectException -> {
                Log.w(TAG, e.stackTraceToString())
                emit(Resource(EventStatus.LOST_INTERNET_ACCESS, null) as T)
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Flow<T>.exceptionCreateWeatherLocation(): Flow<T> {
    return catch { e ->
        when (e) {
            is NullPointerException -> {
                Log.w(TAG, e.stackTraceToString())
                if (e.message == "Request limit exceeded") {
                    emit(Resource(EventStatus.REQUEST_LIMIT_EXCEEDED, null) as T )
                } else {
                    emit(Resource(EventStatus.LOCATION_INFO_FAILURE, null) as T )
                }
            }

            is ConnectException -> {
                Log.w(TAG, e.stackTraceToString())
                emit(Resource(EventStatus.LOST_INTERNET_ACCESS, null) as T)
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T1, T2> Flow<T1>.exceptionUpdateWeather(weatherCityData: T2): Flow<T1> {
    return catch { e ->
        when (e) {
            is ConnectException -> {
                Log.w(TAG, e.stackTraceToString())
                emit(Resource(EventStatus.LOST_INTERNET_ACCESS, weatherCityData) as T1)
            }

            is SSLException -> {
                Log.w(TAG, e.stackTraceToString())
                emit(Resource(EventStatus.CITY_WEATHER_UPDATE_FAILED, weatherCityData) as T1)
            }
        }
    }
}