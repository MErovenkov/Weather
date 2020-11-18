package com.example.weather.model

import com.example.weather.repository.dao.WeatherCityDao
import com.example.weather.utils.DBNaming
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = DBNaming.WeatherCityEntry.TABLE_NAME, daoClass = WeatherCityDao::class)
class WeatherCity {

    @DatabaseField(generatedId = true, columnName = DBNaming.WeatherCityEntry.COLUMN_ID)
    var id: Int = 0

    @DatabaseField(uniqueCombo = true, columnName = DBNaming.WeatherCityEntry.COLUMN_NAME_CITY)
    lateinit var nameCity: String

    @DatabaseField(uniqueCombo = true, columnName = DBNaming.WeatherCityEntry.COLUMN_IS_CURRENT_LOCATION)
    var isCurrentLocation: Boolean = false

    @DatabaseField(columnName = DBNaming.WeatherCityEntry.COLUMN_WEATHER_CURRENT, foreign = true,
        foreignAutoRefresh = true, foreignAutoCreate = true)
    lateinit var weatherCurrent: WeatherCurrent

    @ForeignCollectionField(eager = true)
    lateinit var weatherFutureList: Collection<WeatherFuture>

    constructor()

    constructor(nameCity: String, weatherCurrent: WeatherCurrent,
                weatherFutureList: ArrayList<WeatherFuture>) {
        this.nameCity = nameCity
        this.weatherCurrent = weatherCurrent
        this.weatherFutureList = weatherFutureList
    }
}
