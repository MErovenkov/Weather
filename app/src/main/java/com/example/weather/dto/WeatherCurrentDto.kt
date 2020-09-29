package com.example.weather.dto

import com.beust.klaxon.Json

class WeatherCurrentDto (
    @Json(name = "name")
    var nameCity: String,

    @Json(path = "$.coord.lat")
    var lat: Double,

    @Json(path = "$.coord.lon")
    var lon: Double,

    @Json(path = "$.main.temp")
    var currentTemperature: Double,

    @Json(path = "$.weather[0].icon")
    var nameIconWeather: String,
)