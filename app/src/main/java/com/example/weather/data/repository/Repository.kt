package com.example.weather.data.repository

import com.example.weather.utils.resource.TileData
import com.example.weather.data.repository.api.WeatherData
import com.example.weather.data.repository.dao.OrmLiteHelper
import com.example.weather.data.model.WeatherCity
import com.example.weather.utils.resource.event.EventStatus
import com.example.weather.utils.extensions.*
import com.example.weather.utils.resource.Resource
import io.reactivex.rxjava3.core.Single

class Repository(private val dataBaseHelper: OrmLiteHelper,
                 private val weatherData: WeatherData) {

    fun getWeatherCities(): ArrayList<WeatherCity> =
        dataBaseHelper.getWeatherCities()

    fun getWeatherCityByName(nameCity: String): WeatherCity? =
        dataBaseHelper.getWeatherCityByName(nameCity)

    fun createWeatherCity(nameCity: String): Single<Resource<WeatherCity>> = Single.fromCallable {
        val newWeatherCity = weatherData.getWeatherCity(nameCity)

        return@fromCallable Resource(EventStatus.CITY_ADDED,
                                     dataBaseHelper.createWeatherCity(newWeatherCity))
    }.exceptionCreateWeather()

    fun updateWeatherCity(weatherCity: WeatherCity)
            : Single<Resource<WeatherCity>> = Single.fromCallable  {
        val newWeatherCity = weatherData.getUpdateWeatherCity(weatherCity)

        return@fromCallable Resource(EventStatus.CITY_WEATHER_DATA_UPDATED,
                                     dataBaseHelper.updateWeatherCity(newWeatherCity))
    }.exceptionUpdateWeather(weatherCity)

    fun updateWeatherCities(): Single<Resource<ArrayList<WeatherCity>>> = Single.fromCallable {
        val weatherCityList = weatherData.getUpdatedWeatherCityList(dataBaseHelper.getWeatherCities())

        if (weatherCityList.isNotEmpty()) {
            return@fromCallable Resource(EventStatus.CITY_WEATHER_DATA_UPDATED,
                dataBaseHelper.updateRecyclerCitiesWeather(weatherCityList))

        } else return@fromCallable Resource(EventStatus.IS_NOT_REFRESHING,
            dataBaseHelper.getWeatherCities())
    }.exceptionUpdateWeather(dataBaseHelper.getWeatherCities())

    fun deletedWeatherCity(weatherCity: WeatherCity) {
        dataBaseHelper.deletedWeatherCity(weatherCity)
    }

    /**
     * Current Location
     * */
    fun getCurrentLocationWeather(): WeatherCity? =
        dataBaseHelper.getCurrentLocationWeather()

    fun createWeatherCurrentLocation(coordinateLat: Double, coordinateLon: Double)
            : Single<Resource<WeatherCity>> = Single.fromCallable {
        val newWeatherCity: WeatherCity = weatherData
                                            .getWeatherCityByCoordinate(coordinateLat, coordinateLon)

        return@fromCallable (Resource(EventStatus.CURRENT_LOCATION_RECEIVED,
            changeWeatherCurrentLocation(newWeatherCity)))
    }.exceptionCreateWeatherLocation()

    fun createWeatherCurrentLocation(nameCity: String)
            : Single<Resource<WeatherCity>> = Single.fromCallable {
        val newWeatherCity: WeatherCity = weatherData.getWeatherCity(nameCity)

        return@fromCallable (Resource(EventStatus.CURRENT_LOCATION_RECEIVED,
                                      changeWeatherCurrentLocation(newWeatherCity)))
    }.exceptionCreateWeatherLocation()

    private fun changeWeatherCurrentLocation(newWeatherCity: WeatherCity): WeatherCity {
        val weatherCurrentLocation = getCurrentLocationWeather()

        if (weatherCurrentLocation != null) {
            dataBaseHelper.deletedWeatherCity(weatherCurrentLocation)
        }

        newWeatherCity.isCurrentLocation = true

        return dataBaseHelper.createWeatherCity(newWeatherCity)
    }

    /**
     * Deep link
     * */
    fun getWeatherCityByDeepLinkData(nameCity: String)
            : Single<Resource<WeatherCity>> = Single.fromCallable {
        val newWeatherCity = weatherData.getWeatherCity(nameCity)
        val oldWeatherCity = getWeatherCityByName(newWeatherCity.nameCity)

        if (oldWeatherCity != null) {
            return@fromCallable (Resource(EventStatus.CITY_WEATHER_DATA_RECEIVED,
                dataBaseHelper.updateWeatherCity(
                weatherData.broadcastingImmutableData(oldWeatherCity, newWeatherCity))))
        } else {
            return@fromCallable (Resource(EventStatus.CITY_WEATHER_DATA_RECEIVED, newWeatherCity))
        }
    }.exceptionGettingWeatherByDeepLink()

    /** Precipitation */
    fun getTileData(layer: String, zoom: Int, x: Int, y: Int)
            : Single<Resource<TileData>> = Single.fromCallable {
        val newTileData = weatherData.getTileData(layer, zoom, x, y)

        return@fromCallable (Resource(EventStatus.PRECIPITATION_TILE_ACCEPTED, newTileData))
    }.exceptionGettingPrecipitation()
}