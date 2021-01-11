package com.example.weather.utils.resource

import com.example.weather.utils.resource.event.Event

class Resource<T> {
    private var event: Event? = null
    private var data: T? = null

    constructor(event: Int, weatherData: T?) {
        this.event = Event(event)
        this.data = weatherData
    }

    constructor(weatherData: T?) {
        this.data = weatherData
    }

    constructor(event: Int) {
        this.event = Event(event)
    }

    fun getData(): T? {
        return data
    }

    fun getEvent(): Event? {
        return event
    }
}