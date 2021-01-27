package com.example.weather.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherFutureDto (
    @Json(name = "daily")
    val days: List<Day>,

    @Json(name = "alerts")
    val alerts: List<Alert>
) {
    data class Alert(
        @Json(name = "start")
        val start: Long,

        @Json(name = "end")
        val end: Long,

        @Json(name = "description")
        val description: String
    )

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