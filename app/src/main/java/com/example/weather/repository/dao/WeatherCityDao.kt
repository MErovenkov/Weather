package com.example.weather.repository.dao

import com.example.weather.model.WeatherCity
import com.example.weather.utils.DBNaming
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
                .where().eq(DBNaming.WeatherCityEntry.COLUMN_NAME_CITY, nameCity)
                .and().eq(DBNaming.WeatherCityEntry.COLUMN_IS_CURRENT_LOCATION, false).prepare())
    }

    fun getWeatherCurrentLocation(): WeatherCity {
        return this.queryForFirst(
            this.queryBuilder()
                .where().eq(DBNaming.WeatherCityEntry.COLUMN_IS_CURRENT_LOCATION, true).prepare())
    }
}