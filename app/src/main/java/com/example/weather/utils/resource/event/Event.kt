package com.example.weather.utils.resource.event

class Event(private val status: Int) {
    private var hasBeenHandled = false

    fun getStatusIfNotHandled(): Int? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            status
        }
    }
}