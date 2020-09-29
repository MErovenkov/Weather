package com.example.weather.utils.api

import android.content.Context
import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.example.weather.R
import com.example.weather.dto.WeatherCurrentDto
import com.example.weather.dto.WeatherFutureDto
import org.json.JSONObject
import java.io.StringReader
import java.net.URL

class WeatherApiRequester(context: Context) {
    private val CURRENT_WEATHER_DATA_RESPONSE =
        "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=ru&appid=%s"
    private val WEATHER_NEAR_FUTURE_RESPONSE =
        "https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s" +
                "&exclude=minutely,hourly,current&units=metric&lang=ru&appid=%s"

    private val klaxon = Klaxon()
    private val apiKayWeatherBit: String = context.getString(R.string.open_weather_map_api_key)

    fun getWeatherCurrentDto(nameCity: String): WeatherCurrentDto {
            return klaxon.parse<WeatherCurrentDto>(
                URL(String.format(CURRENT_WEATHER_DATA_RESPONSE, nameCity, apiKayWeatherBit))
                    .readText()
            )!!
    }

    fun getWeatherFutureDtoList(coordinateCityLat: Double, coordinateCityLon: Double)
            : ArrayList<WeatherFutureDto> {
        val weatherFutureDtoList: ArrayList<WeatherFutureDto> = ArrayList()

        val response = JSONObject(
            URL(String.format(
                WEATHER_NEAR_FUTURE_RESPONSE,
                coordinateCityLat,
                coordinateCityLon,
                apiKayWeatherBit)
            ).readText()
        ).getJSONArray("daily").toString()

        JsonReader(StringReader(response)).use { reader ->
            reader.beginArray {
                while (reader.hasNext()) {
                    val product = klaxon.parse<WeatherFutureDto>(reader)
                    weatherFutureDtoList.add(product!!)
                }
            }
        }
        return weatherFutureDtoList
    }
}