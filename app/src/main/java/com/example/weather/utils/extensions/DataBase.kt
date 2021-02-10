package com.example.weather.utils.extensions

import com.example.weather.data.model.WeatherCity
import com.example.weather.data.repository.dao.OrmLiteHelper

/**
 * DB extension
 */
fun OrmLiteHelper.getWeatherCities(): ArrayList<WeatherCity> {
    return getWeatherCityDao().getWeatherCities()
}

fun OrmLiteHelper.getWeatherCityByName(nameCity: String): WeatherCity? {
    return getWeatherCityDao().getWeatherCityByName(nameCity)
}

fun OrmLiteHelper.getCurrentLocationWeather(): WeatherCity? {
    return getWeatherCityDao().getWeatherCurrentLocation()
}