package com.example.weather.dto

import com.beust.klaxon.Json

class WeatherFutureDto (
    @Json(name = "dt")
    var date: Long,

    @Json(path = "$.temp.max")
    var temperatureMax: Double,

    @Json(path = "$.temp.min")
    var temperatureMin: Double,

    @Json(path = "$.weather[0].icon")
    var nameIconWeather: String
)