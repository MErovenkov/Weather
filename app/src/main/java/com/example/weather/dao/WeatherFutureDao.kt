package com.example.weather.dao

import com.example.weather.model.WeatherFuture
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource
import java.sql.SQLException

class WeatherFutureDao @Throws(SQLException::class) constructor(connectionSource: ConnectionSource)
    : BaseDaoImpl<WeatherFuture, Int>(WeatherFuture::class.java) {
    init {
        setConnectionSource(connectionSource)
        initialize()
    }
}