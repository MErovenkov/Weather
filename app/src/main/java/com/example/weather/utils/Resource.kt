package com.example.weather.utils

class Resource<T> {
    private var event: Int? = null
    private var data: T? = null

    constructor(event: Int?, weatherData: T?) {
        this.event = event
        this.data = weatherData
    }

    constructor(weatherData: T?) {
        this.data = weatherData
    }
    constructor(event: Int?) {
        this.event = event
    }

    fun getData(): T? {
        return data
    }

    fun getEvent(): Int? {
        return event
    }
}