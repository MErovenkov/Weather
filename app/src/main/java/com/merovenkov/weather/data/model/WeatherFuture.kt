package com.merovenkov.weather.data.model

import com.merovenkov.weather.data.repository.dao.WeatherFutureDao
import com.merovenkov.weather.utils.DBNaming
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = DBNaming.WeatherFutureEntry.TABLE_NAME, daoClass = WeatherFutureDao::class)
class WeatherFuture {

    @DatabaseField(generatedId = true, columnName = DBNaming.WeatherFutureEntry.COLUMN_ID)
    var id: Int = 0

    @DatabaseField(columnName = DBNaming.WeatherFutureEntry.COLUMN_NAME_DAY)
    lateinit var nameDay: String

    @DatabaseField(columnName = DBNaming.WeatherFutureEntry.COLUMN_TEMPERATURE_MAX)
    lateinit var temperatureMax: String

    @DatabaseField(columnName = DBNaming.WeatherFutureEntry.COLUMN_TEMPERATURE_MIN)
    lateinit var temperatureMin: String

    @DatabaseField(columnName = DBNaming.WeatherFutureEntry.COLUMN_NAME_IC_WEATHER)
    lateinit var nameIconWeather: String

    @DatabaseField(columnName = DBNaming.WeatherFutureEntry.COLUMN_CITY_ID, foreign = true)
    lateinit var weatherCity: WeatherCity

    constructor()

    constructor(nameDay: String, temperatureMax: String,
                temperatureMin: String, nameIconWeather: String) {
        this.nameDay = nameDay
        this.temperatureMax = temperatureMax
        this.temperatureMin = temperatureMin
        this.nameIconWeather = nameIconWeather
    }
}