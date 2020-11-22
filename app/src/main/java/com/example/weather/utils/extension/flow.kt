package com.example.weather.utils.extension

import android.util.Log
import com.example.weather.utils.EventStatus
import com.example.weather.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import java.net.ConnectException
import java.sql.SQLException
import javax.net.ssl.SSLException

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
fun <T> Flow<T>.createWeatherException(nameCity: String): Flow<T> {
    return catch { e ->
        when (e) {
            is NullPointerException -> {
                Log.w("$e nameCity: $nameCity", e.stackTraceToString())
                emit(Resource(EventStatus.CITY_NOT_FOUND, null) as T )
            }

            is SQLException -> {
                Log.w("$e nameCity: $nameCity", e.stackTraceToString())
                emit(Resource(EventStatus.CITY_EXIST, null) as T)
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Flow<T>.connectException(): Flow<T> {
    return catch { e ->
        when (e) {
            is ConnectException -> {
                Log.e(e.toString(), e.stackTraceToString())
                emit(Resource(EventStatus.LOST_INTERNET_ACCESS, null) as T)
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Flow<T>.sslUpdateException(): Flow<T> {
    return catch { e ->
        when (e) {
            is SSLException -> {
                Log.w(e.toString(), e.stackTraceToString())
                emit(Resource(EventStatus.CITY_WEATHER_UPDATE_FAILED, null) as T)
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Flow<T>.concurrentModificationException(): Flow<T> {
    return catch { e ->
        when (e) {
            is ConcurrentModificationException -> {
                Log.e(e.toString(), e.stackTraceToString())
                emit(Resource(EventStatus.CITY_WEATHER_UPDATE_FAILED, null) as T)
            }

        }
    }
}

