package com.merovenkov.weather.ui.navigation

interface IWeatherNavigation {
    fun openDetails(nameCity: String, isCurrentLocation: Boolean,
                    hasAnimationOpening: Boolean)
    fun openDetailsByDeepLinkData(nameCity: String)
}