package com.example.weather.utils.resource.event

import com.example.weather.R

object EventStatus {
    const val CITY_ADDED = R.string.city_added
    const val CITY_WEATHER_DATA_UPDATED = R.string.city_weather_data_updated
    const val CITY_EXIST = R.string.city_exist
    const val CITY_NOT_FOUND = R.string.city_not_found
    const val CITY_WEATHER_UPDATE_FAILED = R.string.city_weather_update_failed
    const val LOST_INTERNET_ACCESS = R.string.lost_internet_access

    const val LOCATION_INFO_FAILURE = R.string.location_information_failure

    const val IS_NOT_REFRESHING = 0
    const val CURRENT_LOCATION_RECEIVED = 1
}