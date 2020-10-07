package com.example.weather.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherFutureDto (
    @Json(name = "daily")
    val days: List<Day>
) {
    data class Day(
        @Json(name = "dt")
        val date: Long,

        @Json(name = "temp")
        val temperature: Temp,

        @Json(name = "weather")
        val weatherInfo: List<Weather>,
    ) {
        data class Temp(
            @Json(name = "max") val max: Double,
            @Json(name = "min") val min: Double
        )

        data class Weather (
            @Json(name = "icon") val nameIconWeather: String
        )
    }
}