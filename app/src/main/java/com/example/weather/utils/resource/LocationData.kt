package com.example.weather.utils.resource

data class LocationData (val lat: Double? = null,
                         val lon: Double? = null) {
    var name: String? = null
        private set

    constructor(name: String) : this() {
        this.name = name
    }
}