package com.merovenkov.weather.ui.navigation

interface IDetailedWeatherNavigation {
    fun openPrecipitationMap(cityName: String, lat: String, lon: String)
    fun popBackStack()
}