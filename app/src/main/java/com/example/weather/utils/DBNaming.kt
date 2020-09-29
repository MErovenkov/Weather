package com.example.weather.utils

class DBNaming private constructor() {
    object DB {
        const val DATABASE_NAME = "weathers.db"
    }

    object WeatherCityEntry {
        const val TABLE_NAME = "city_weather"
        const val COLUMN_ID = "id_city"
        const val COLUMN_NAME_CITY = "name_city"
        const val COLUMN_WEATHER_CURRENT = "weather_current"
    }

    object WeatherCurrentEntry {
        const val TABLE_NAME = "tb_weather_current"
        const val COLUMN_ID = "id_current"
        const val COLUMN_TEMPERATURE = "temperature"
        const val COLUMN_NAME_IC_WEATHER = "name_ic_weather"
    }

    object WeatherFutureEntry {
        const val TABLE_NAME = "tb_weather_future"
        const val COLUMN_ID = "id_future"
        const val COLUMN_NAME_DAY = "name_day"
        const val COLUMN_TEMPERATURE_MAX = "temperature_max"
        const val COLUMN_TEMPERATURE_MIN = "temperature_min"
        const val COLUMN_NAME_IC_WEATHER = "name_ic_weather"
        const val COLUMN_CITY_ID = "city_id"
    }
}