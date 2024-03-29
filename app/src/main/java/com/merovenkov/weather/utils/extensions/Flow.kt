package com.merovenkov.weather.utils.extensions

import android.util.Log
import android.content.res.Resources.NotFoundException
import com.merovenkov.weather.utils.exception.OverLimitApiKeyException
import com.merovenkov.weather.utils.resource.event.EventStatus
import com.merovenkov.weather.utils.resource.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
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
        Log.w(TAG, e.stackTraceToString())
        when (e) {
            is NotFoundException -> emit(Resource(EventStatus.CITY_NOT_FOUND, null) as T)
            is SQLException -> emit(Resource(EventStatus.CITY_EXIST, null) as T)
            is ConnectException -> emit(Resource(EventStatus.LOST_INTERNET_ACCESS, null) as T)
            is OverLimitApiKeyException -> emit(Resource(EventStatus.REQUEST_LIMIT_EXCEEDED, null) as T)
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Flow<T>.exceptionCreateWeatherLocation(): Flow<T> {
    return catch { e ->
        Log.w(TAG, e.stackTraceToString())
        when (e) {
            is NotFoundException -> emit(Resource(EventStatus.LOCATION_INFO_FAILURE, null) as T)
            is ConnectException -> emit(Resource(EventStatus.LOST_INTERNET_ACCESS, null) as T)
            is OverLimitApiKeyException -> emit(Resource(EventStatus.REQUEST_LIMIT_EXCEEDED, null) as T)
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T1, T2> Flow<T1>.exceptionUpdateWeather(weatherCityData: T2): Flow<T1> {
    return catch { e ->
        Log.w(TAG, e.stackTraceToString())
        when (e) {
            is SSLException -> emit(Resource(EventStatus.CITY_WEATHER_UPDATE_FAILED,
                weatherCityData) as T1)

            is ConnectException -> emit(Resource(EventStatus.LOST_INTERNET_ACCESS,
                weatherCityData) as T1)

            is OverLimitApiKeyException -> emit(Resource(EventStatus.REQUEST_LIMIT_EXCEEDED,
                weatherCityData) as T1 )
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Flow<T>.exceptionGettingWeatherByDeepLink(): Flow<T> {
    return catch { e ->
        Log.w(TAG, e.stackTraceToString())
        when (e) {
            is NotFoundException -> emit(Resource(EventStatus.CITY_NOT_FOUND, null) as T)
            is ConnectException -> emit(Resource(EventStatus.LOST_INTERNET_ACCESS, null) as T)
            is OverLimitApiKeyException -> emit(Resource(EventStatus.REQUEST_LIMIT_EXCEEDED, null) as T)
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Flow<T>.exceptionGettingPrecipitation(): Flow<T> {
    return catch { e ->
        Log.w(TAG, e.stackTraceToString())
        when (e) {
            is NotFoundException -> emit(Resource(EventStatus.PRECIPITATION_TILE_FAILURE, null) as T)
            is SocketTimeoutException -> emit(Resource(EventStatus.LOST_INTERNET_ACCESS, null) as T)
            is UnknownHostException -> emit(Resource(EventStatus.LOST_INTERNET_ACCESS, null) as T)
            is ConnectException -> emit(Resource(EventStatus.LOST_INTERNET_ACCESS, null) as T)
            is OverLimitApiKeyException -> emit(Resource(EventStatus.REQUEST_LIMIT_EXCEEDED, null) as T)
        }
    }
}