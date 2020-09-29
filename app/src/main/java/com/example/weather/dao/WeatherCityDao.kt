package com.example.weather.dao

import com.example.weather.model.WeatherCity
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource
import java.sql.SQLException

class WeatherCityDao @Throws(SQLException::class) constructor(connectionSource: ConnectionSource)
    : BaseDaoImpl<WeatherCity, Int>(WeatherCity::class.java) {
    init {
        setConnectionSource(connectionSource)
        initialize()
    }

    fun getWeatherCityByName(nameCity: String): WeatherCity {
        return this.queryForFirst(
            this.queryBuilder()
            .where().eq("name_city", nameCity).prepare())
    }
}