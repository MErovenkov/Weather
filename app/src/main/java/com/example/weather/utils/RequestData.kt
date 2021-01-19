package com.example.weather.utils

data class RequestData(val nameCity: String? = null) {
    var coordinateLat: String? = null
        private set

    var coordinateLon: String? = null
        private set

    var isCurrent: Boolean = false
        private set

    constructor(coordinateLat: String, coordinateLon: String, isCurrent: Boolean) : this() {
        this.coordinateLat = coordinateLat
        this.coordinateLon = coordinateLon
        this.isCurrent = isCurrent
    }
}