package com.example.weather.utils

import android.content.Context

object WeatherDataHelper {
    private var mContext: Context? = null

    fun getWeatherData(): WeatherData {
        return WeatherData(mContext!!)
    }

    fun setContext(context: Context) {
        this.mContext = context
    }
}