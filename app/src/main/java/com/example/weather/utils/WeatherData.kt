package com.example.weather.utils

import android.content.Context
import android.util.Log
import com.example.weather.R
import com.example.weather.model.WeatherCity
import com.example.weather.utils.api.NetworkService
import com.example.weather.utils.api.WeatherApiRequester
import java.net.ConnectException
import kotlin.collections.ArrayList

class WeatherData private constructor() {
    private var weatherApiRequester: WeatherApiRequester? = null
    private var mapperWeatherData: MapperWeatherData? = null

    private object HOLDER {
        val INSTANCE: WeatherData = WeatherData()
    }

    companion object {
        private var mContext: Context? = null
        val instance: WeatherData by lazy { HOLDER.INSTANCE }

        fun setContext(context: Context) {
            this.mContext = context.applicationContext
        }
    }

    init {
        weatherApiRequester = WeatherApiRequester(NetworkService(mContext!!.resources.openRawResource(
            R.raw.certificate_openweathermap)), mContext!!.getString(R.string.open_weather_map_api_key))
        mapperWeatherData = MapperWeatherData(mContext!!)
    }

    fun getWeatherCity(nameCity: String): WeatherCity {
       try {
            val weatherCurrentDto = weatherApiRequester!!.getWeatherCurrentDto(nameCity)
            val weatherFutureDto = weatherApiRequester!!.getWeatherFutureDto(
                weatherCurrentDto.coordinatesCity.lat, weatherCurrentDto.coordinatesCity.lon)

           return mapperWeatherData!!.getWeatherCity(weatherCurrentDto, weatherFutureDto)
       } catch (e: NullPointerException) {
            Log.w("$e nameCity: $nameCity", Thread.currentThread().stackTrace[2].toString())
            throw NullPointerException()
       } catch (e: ConnectException) {
           Log.w(e.toString(), Thread.currentThread().stackTrace[2].toString())
           throw ConnectException()
       }
    }

    fun getUpdateWeatherCity(oldWeatherCity: WeatherCity): WeatherCity {
        return changingId(oldWeatherCity, getWeatherCity(oldWeatherCity.nameCity))
    }

    fun getUpdatedWeatherCityList(oldWeatherCityList: ArrayList<WeatherCity>): ArrayList<WeatherCity>{
        try {
            val newWeatherCityList: ArrayList<WeatherCity> = ArrayList()

            for (oldWeatherCity in oldWeatherCityList) {
                val newWeatherCity = getWeatherCity(oldWeatherCity.nameCity)
                newWeatherCityList.add(changingId(oldWeatherCity, newWeatherCity))
            }
            return newWeatherCityList
        } catch (e: ConnectException) {
            Log.w(e.toString(), Thread.currentThread().stackTrace[2].toString())
            throw ConnectException()
        }
    }

    private fun changingId(oldWeatherCity: WeatherCity, newWeatherCity: WeatherCity)
            : WeatherCity {
        newWeatherCity.id = oldWeatherCity.id
        newWeatherCity.weatherCurrent.id = oldWeatherCity.weatherCurrent.id

        val newWeatherFutureList = ArrayList(newWeatherCity.weatherFutureList)
        val oldWeatherFutureList = ArrayList(oldWeatherCity.weatherFutureList)
        for (newWeatherFuture in newWeatherFutureList.indices) {
            for (oldWeatherFuture in oldWeatherCity.weatherFutureList.indices) {
                if (newWeatherFuture == oldWeatherFuture) {
                    newWeatherFutureList[newWeatherFuture].id =
                        oldWeatherFutureList[oldWeatherFuture].id
                }
            }
        }

        return newWeatherCity
    }
}

