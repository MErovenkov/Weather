package com.merovenkov.weather.utils.extensions

import com.merovenkov.weather.data.model.WeatherCity
import com.merovenkov.weather.data.repository.dao.OrmLiteHelper

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