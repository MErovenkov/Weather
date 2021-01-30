package com.example.weather.data.repository.dao

import com.example.weather.data.model.WeatherCurrent
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource
import java.sql.SQLException

class WeatherCurrentDao @Throws(SQLException::class) constructor(connectionSource: ConnectionSource)
    : BaseDaoImpl<WeatherCurrent, Int>(WeatherCurrent::class.java) {
    init {
        setConnectionSource(connectionSource)
        initialize()
    }
}