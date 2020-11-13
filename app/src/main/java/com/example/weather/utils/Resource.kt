package com.example.weather.utils

class Resource<T> {
    private var event: Int? = null
    private var weatherData: T? = null

    constructor(event: Int?, weatherData: T?) {
        this.event = event
        this.weatherData = weatherData
    }

    constructor(weatherData: T?) {
        this.weatherData = weatherData
    }
    constructor(event: Int?) {
        this.event = event
    }

    fun getData(): T? {
        return weatherData
    }

    fun getEvent(): Int? {
        return event
    }
}