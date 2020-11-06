package com.example.weather.model

import com.example.weather.repository.dao.WeatherCurrentDao
import com.example.weather.utils.DBNaming
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = DBNaming.WeatherCurrentEntry.TABLE_NAME, daoClass = WeatherCurrentDao::class)
class WeatherCurrent {

    @DatabaseField(generatedId = true, columnName = DBNaming.WeatherCurrentEntry.COLUMN_ID)
    var id: Int = 0

    @DatabaseField(columnName = DBNaming.WeatherCurrentEntry.COLUMN_TEMPERATURE)
    lateinit var temperature: String

    @DatabaseField(columnName = DBNaming.WeatherCurrentEntry.COLUMN_NAME_IC_WEATHER)
    lateinit var nameIconWeather: String

    constructor()

    constructor(temperature: String, nameIconWeather: String) {
        this.temperature = temperature
        this.nameIconWeather = nameIconWeather
    }
}

