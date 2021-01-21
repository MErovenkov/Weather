package com.example.weather.utils

import android.content.Context
import com.example.weather.R
import java.lang.NullPointerException

class ApiKeyChanger(private val context: Context) {
    private var apiKeyList: ArrayList<String> = ArrayList()
    private var keyPosition: Int = 0
    private var attempts: Int = 0

    init {
        apiKeyList.apply {
            add(context.getString(R.string.open_weather_map_api_key_1))
            add(context.getString(R.string.open_weather_map_api_key_2))
            add(context.getString(R.string.open_weather_map_api_key_3))
        }
    }

    fun getApiKey(): String {
        return apiKeyList[keyPosition]
    }

    fun changeApiKey(): Boolean {
        if (keyPosition == apiKeyList.size - 1 && attempts == apiKeyList.size) {
            attempts = 0
            throw NullPointerException("Request limit exceeded")
        } else if (keyPosition == apiKeyList.size - 1 && attempts != apiKeyList.size) {
            keyPosition = 0
        } else {
            keyPosition++
        }

        attempts++

        return true
    }
}