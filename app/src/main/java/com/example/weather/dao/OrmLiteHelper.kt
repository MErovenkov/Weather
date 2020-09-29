package com.example.weather.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.weather.model.WeatherCity
import com.example.weather.model.WeatherCurrent
import com.example.weather.model.WeatherFuture
import com.example.weather.utils.DBNaming
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import java.sql.SQLException

class OrmLiteHelper(context: Context)
    : OrmLiteSqliteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private var weatherCityDao: WeatherCityDao = WeatherCityDao(this.connectionSource)
    private var weatherCurrentDao: WeatherCurrentDao = WeatherCurrentDao(this.connectionSource)
    private var weatherFutureDao: WeatherFutureDao = WeatherFutureDao(this.connectionSource)

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = DBNaming.DB.DATABASE_NAME
    }

    override fun onCreate(database: SQLiteDatabase?, connectionSource: ConnectionSource?) {
        try {
            database?.execSQL("PRAGMA foreign_keys=ON;")
            TableUtils.createTable(connectionSource, WeatherCity::class.java)
            TableUtils.createTable(connectionSource, WeatherCurrent::class.java)
            TableUtils.createTable(connectionSource, WeatherFuture::class.java)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(database: SQLiteDatabase?, connectionSource: ConnectionSource?,
        oldVersion: Int, newVersion: Int
    ) {
        try {
            TableUtils.dropTable<WeatherCity, Any>(
                connectionSource,
                WeatherCity::class.java, false
            )
            TableUtils.dropTable<WeatherCurrent, Any>(
                connectionSource,
                WeatherCurrent::class.java, false
            )
            TableUtils.dropTable<WeatherFuture, Any>(
                connectionSource,
                WeatherFuture::class.java, false
            )
            onCreate(database, connectionSource)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun updateCity(newWeatherCity: WeatherCity) {
        weatherCityDao.update(newWeatherCity)
        weatherCurrentDao.update(newWeatherCity.weatherCurrent)
        for (newWeatherFuture in newWeatherCity.weatherFutureList) {
            newWeatherFuture.weatherCity = newWeatherCity
            weatherFutureDao.update(newWeatherFuture)
        }
    }

    fun changesAllData(newWeatherCityList: ArrayList<WeatherCity>) {
        val oldWeatherCityList = ArrayList(this.getWeatherCityDao().queryForAll())

        for (oldWeatherCity in oldWeatherCityList) {
            if (newWeatherCityList.stream().noneMatch{ weatherCity ->
                    weatherCity.nameCity == oldWeatherCity.nameCity }) {
                this.deletedWeatherCity(oldWeatherCity)
            }
        }

        for (weatherCity in newWeatherCityList) {
            if (oldWeatherCityList.stream().noneMatch{ oldWeatherCity ->
                    oldWeatherCity.nameCity == weatherCity.nameCity}) {
                this.createCity(weatherCity)
            } else if (oldWeatherCityList.stream().anyMatch{ oldWeatherCity ->
                    oldWeatherCity.nameCity == weatherCity.nameCity}) {
                this.updateCity(weatherCity)
            }
        }
    }

    fun getWeatherCityDao(): WeatherCityDao {
        return weatherCityDao
    }

    private fun createCity(newWeatherCity: WeatherCity) {
        weatherCityDao.create(newWeatherCity)
        for (future in newWeatherCity.weatherFutureList) {
            future.weatherCity = newWeatherCity
            weatherFutureDao.create(future)
        }
    }

    private fun deletedWeatherCity(weatherCity: WeatherCity) {
        weatherCityDao.deleteById(weatherCity.id)
        weatherCurrentDao.delete(weatherCity.weatherCurrent)
        for (weatherFuture in weatherCity.weatherFutureList) {
            weatherFutureDao.delete(weatherFuture)
        }
    }
}