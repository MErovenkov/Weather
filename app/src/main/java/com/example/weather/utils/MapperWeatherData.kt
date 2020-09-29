package com.example.weather.utils

import android.content.Context
import com.example.weather.R
import com.example.weather.dto.WeatherCurrentDto
import com.example.weather.dto.WeatherFutureDto
import com.example.weather.model.WeatherCity
import com.example.weather.model.WeatherCurrent
import com.example.weather.model.WeatherFuture
import java.util.*
import kotlin.collections.ArrayList

class MapperWeatherData(private val context: Context) {

    fun getWeatherCity(
        weatherCurrentDto: WeatherCurrentDto,
        weatherCurrentDtoList: ArrayList<WeatherFutureDto>
    ): WeatherCity {
        return WeatherCity(
            weatherCurrentDto.nameCity,
            getWeatherCurrent(weatherCurrentDto),
            getWeatherFutureList(weatherCurrentDtoList)
        )
    }

    private fun getWeatherCurrent(weatherCurrentDto: WeatherCurrentDto): WeatherCurrent {
        return WeatherCurrent(
            getTemperatureCelsius(weatherCurrentDto.currentTemperature),
            getValidNameIcon(weatherCurrentDto.nameIconWeather)
        )
    }

    private fun getWeatherFutureList(weatherCurrentDtoList: ArrayList<WeatherFutureDto>)
            : ArrayList<WeatherFuture> {
        val weatherFutureList: ArrayList<WeatherFuture> = ArrayList()

        for (weatherFutureDto in weatherCurrentDtoList) {
            weatherFutureList.add(
                WeatherFuture(
                    getNameDay(weatherFutureDto.date),
                    getTemperatureCelsius(weatherFutureDto.temperatureMax),
                    getTemperatureCelsius(weatherFutureDto.temperatureMin),
                    getValidNameIcon(weatherFutureDto.nameIconWeather)
                )
            )
        }
        return weatherFutureList
    }

    private fun getTemperatureCelsius(temperature: Double): String {
        return "${temperature.toInt()}${context.getString(R.string.temperature)}C"
    }

    private fun getValidNameIcon(nameIconWeather: String): String {
        return "w$nameIconWeather"
    }

    private fun getNameDay(data: Long): String {
        val calendar = Calendar.getInstance()
        calendar.time = Date(data * 1000)

        return when((calendar[Calendar.DAY_OF_WEEK] - 1).toString()) {
            "1" -> context.getString(R.string.monday)
            "2" -> context.getString(R.string.tuesday)
            "3" -> context.getString(R.string.wednesday)
            "4" -> context.getString(R.string.thursday)
            "5" -> context.getString(R.string.friday)
            "6" -> context.getString(R.string.saturday)
            else -> context.getString(R.string.sunday)
        }
    }
}