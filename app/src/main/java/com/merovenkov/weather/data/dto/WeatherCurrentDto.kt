package com.merovenkov.weather.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherCurrentDto (

    @Json(name = "name")
    val nameCity: String,

    @Json(name = "coord")
    val coordinatesCity: Coordinates,

    @Json(name = "main")
    val currentTemperature: CurrentTemperature,

    @Json(name = "weather")
    val weatherInfo: List<Weather>,
) {
    data class Coordinates(
        @Json(name = "lat") val lat: String,
        @Json(name = "lon") val lon: String
    )

    data class CurrentTemperature (
        @Json(name = "temp") val temp: Double
    )

    data class Weather (
        @Json(name = "icon") val nameIconWeather: String
    )
}