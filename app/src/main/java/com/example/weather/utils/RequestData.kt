package com.example.weather.utils

data class RequestData(val nameCity: String? = null) {

    /** for api.openweathermap.org request */
    var coordinateLat: String? = null
        private set

    var coordinateLon: String? = null
        private set

    var isCurrent: Boolean = false
        private set

    /** for tile.openweathermap.org request*/
    var isTileUrl: Boolean = false
        private set

    var layer: String? = null
        private set

    var zoom: Int? = null
        private set

    var x: Int? = null
        private set

    var y: Int? = null
        private set

    constructor(coordinateLat: String, coordinateLon: String, isCurrent: Boolean) : this() {
        this.coordinateLat = coordinateLat
        this.coordinateLon = coordinateLon
        this.isCurrent = isCurrent
    }

    constructor(isTileUrl: Boolean, layer: String, zoom: Int, x: Int, y: Int) : this() {
        this.isTileUrl = isTileUrl
        this.layer = layer
        this.zoom = zoom
        this.x = x
        this.y = y
    }
}