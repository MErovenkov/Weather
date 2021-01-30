package com.example.weather.utils

import android.content.Context
import com.example.weather.R
import com.example.weather.data.dto.WeatherCurrentDto
import com.example.weather.data.dto.WeatherFutureDto
import com.example.weather.data.model.WeatherCity
import com.example.weather.data.model.WeatherCurrent
import com.example.weather.data.model.WeatherFuture
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class MapperWeatherData(private val context: Context) {

    companion object {
        private const val SOLO_ELEMENT = 0
        private const val TOMORROW = 1
    }

    fun getWeatherCity(weatherCurrentDto: WeatherCurrentDto,
                               weatherFutureDto: WeatherFutureDto): WeatherCity = runBlocking {
        val alertTomorrow = GlobalScope.async(Dispatchers.IO) { getAlertTomorrow(weatherFutureDto) }
        val weatherCurrent = GlobalScope.async(Dispatchers.IO) { getWeatherCurrent(weatherCurrentDto) }
        val weatherFuture = GlobalScope.async(Dispatchers.IO) { getWeatherFutureList(weatherFutureDto) }

        return@runBlocking WeatherCity(weatherCurrentDto.nameCity, alertTomorrow.await(),
                                       weatherCurrent.await(), weatherFuture.await())
    }

    private fun getWeatherCurrent(weatherCurrentDto: WeatherCurrentDto): WeatherCurrent {
        return WeatherCurrent(getTemperatureCelsius(weatherCurrentDto.currentTemperature.temp),
            weatherCurrentDto.weatherInfo[SOLO_ELEMENT].nameIconWeather
        )
    }

    private fun getWeatherFutureList(weatherFutureDto: WeatherFutureDto): ArrayList<WeatherFuture> {
        val weatherFutureList: ArrayList<WeatherFuture> = ArrayList()

        for (dayFuture in weatherFutureDto.days) {
            weatherFutureList.add(
                WeatherFuture(
                    getNameDay(dayFuture.date),
                    getTemperatureCelsius(dayFuture.temperature.max),
                    getTemperatureCelsius(dayFuture.temperature.min),
                    dayFuture.weatherInfo[SOLO_ELEMENT].nameIconWeather
                )
            )
        }
        return weatherFutureList
    }

    private fun getAlertTomorrow(weatherFutureDto: WeatherFutureDto): String {
        var alertMassage = ""

        for (alert in weatherFutureDto.alerts) {
            if (alert.description.isNotEmpty()
                && isTomorrow(alert.start, alert.end, weatherFutureDto.days[TOMORROW].date)) {

                    alertMassage += "${formatAlertMassage(alert.description)}. "
            }
        }

        return alertMassage
    }

    private fun isTomorrow(start: Long, end: Long, current: Long): Boolean {
        return (current in start..end
                || start >= current && getNameDay(start) == getNameDay(current)
                || current >= end && getNameDay(current) == getNameDay(end))
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

    private fun getTemperatureCelsius(temperature: Double): String {
        return "${temperature.toInt()}${context.getString(R.string.temperature)}C"
    }

    private fun formatAlertMassage(alertMassage: String): String {
        return alertMassage.substring(0, 1).toUpperCase(Locale.ROOT) +
                alertMassage.substring(1).toLowerCase(Locale.ROOT)
    }
}