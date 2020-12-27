package com.example.weather.repository.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.weather.model.WeatherCity
import com.example.weather.model.WeatherCurrent
import com.example.weather.model.WeatherFuture
import com.example.weather.utils.DBNaming
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import java.sql.SQLException

class OrmLiteHelper(context: Context)
    : OrmLiteSqliteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    private val weatherCityDao = WeatherCityDao(connectionSource)
    private val weatherCurrentDao = WeatherCurrentDao(connectionSource)
    private val weatherFutureDao = WeatherFutureDao(connectionSource)

    companion object {
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = DBNaming.DB.DATABASE_NAME
    }

    override fun onCreate(database: SQLiteDatabase?, connectionSource: ConnectionSource?) {
        try {
            database?.execSQL("PRAGMA foreign_keys=ON;")
            TableUtils.createTable(connectionSource, WeatherCity::class.java)
            TableUtils.createTable(connectionSource, WeatherCurrent::class.java)
            TableUtils.createTable(connectionSource, WeatherFuture::class.java)
        } catch (e: SQLException) {
            Log.w(e.toString(),  e.stackTraceToString())
        }
    }

    override fun onUpgrade(database: SQLiteDatabase?, connectionSource: ConnectionSource?,
        oldVersion: Int, newVersion: Int
    ) {
        try {
            if (newVersion == 2) {
                weatherCityDao.executeRaw("ALTER TABLE city_weather " +
                        "RENAME TO ${DBNaming.WeatherCityEntry.TABLE_NAME}")
                weatherCityDao.executeRaw(
                    "ALTER TABLE ${DBNaming.WeatherCityEntry.TABLE_NAME} " +
                            "ADD COLUMN ${DBNaming.WeatherCityEntry.COLUMN_IS_CURRENT_LOCATION} INTEGER")
            }
        } catch (e: SQLException) {
            Log.w(e.toString(),  e.stackTraceToString())
        }
    }

    fun createWeatherCity(newWeatherCity: WeatherCity): WeatherCity {
        try {
            weatherCityDao.create(newWeatherCity)
            weatherCurrentDao.create(newWeatherCity.weatherCurrent)
            for (future in newWeatherCity.weatherFutureList) {
                future.weatherCity = newWeatherCity
                weatherFutureDao.create(future)
            }

            return newWeatherCity
        } catch (e: SQLException) {
            Log.w("City exist: ${newWeatherCity.nameCity}", e.stackTraceToString())
            throw SQLException()
        }
    }

    fun updateWeatherCity(newWeatherCity: WeatherCity): WeatherCity {
        weatherCityDao.update(newWeatherCity)
        weatherCurrentDao.update(newWeatherCity.weatherCurrent)
        for (newWeatherFuture in newWeatherCity.weatherFutureList) {
            newWeatherFuture.weatherCity = newWeatherCity
            weatherFutureDao.update(newWeatherFuture)
        }

        return newWeatherCity
    }

    fun updateRecyclerCitiesWeather(newWeatherCityList: ArrayList<WeatherCity>)
            : ArrayList<WeatherCity> {
        for (weatherCity in newWeatherCityList) {
            updateWeatherCity(weatherCity)
        }

        return newWeatherCityList
    }

    fun deletedWeatherCity(weatherCity: WeatherCity) {
        weatherCityDao.deleteById(weatherCity.id)
        weatherCurrentDao.delete(weatherCity.weatherCurrent)
        for (weatherFuture in weatherCity.weatherFutureList) {
            weatherFutureDao.delete(weatherFuture)
        }
    }

    fun getWeatherCityDao(): WeatherCityDao {
        return weatherCityDao
    }
}