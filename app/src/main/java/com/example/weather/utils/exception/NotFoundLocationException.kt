package com.example.weather.utils.exception

class NotFoundLocationException: Exception {
    constructor(): super()
    constructor(massage: String): super(massage)
}