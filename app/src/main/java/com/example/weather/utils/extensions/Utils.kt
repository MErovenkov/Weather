package com.example.weather.utils.extensions

import com.example.weather.utils.CheckStatusNetwork

fun CheckStatusNetwork.isNetworkAvailable(): Boolean {
    return getNetworkAvailable().value
}