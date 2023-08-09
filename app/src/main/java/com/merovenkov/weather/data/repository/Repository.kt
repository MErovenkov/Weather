package com.merovenkov.weather.data.repository

import com.merovenkov.weather.utils.resource.TileData
import com.merovenkov.weather.data.repository.api.WeatherData
import com.merovenkov.weather.data.repository.dao.OrmLiteHelper
import com.merovenkov.weather.data.model.WeatherCity
import com.merovenkov.weather.utils.resource.event.EventStatus
import com.merovenkov.weather.utils.extensions.*
import com.merovenkov.weather.utils.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

@Suppress("BlockingMethodInNonBlockingContext")
class Repository(private val dataBaseHelper: OrmLiteHelper,
                 private val weatherData: WeatherData) {

    fun getWeatherCities(): ArrayList<WeatherCity> =
        dataBaseHelper.getWeatherCities()

    fun getWeatherCityByName(nameCity: String): WeatherCity? =
        dataBaseHelper.getWeatherCityByName(nameCity)

    fun createWeatherCity(nameCity: String): Flow<Resource<WeatherCity>> = flow {
        val newWeatherCity = withContext(Dispatchers.IO) {
            weatherData.getWeatherCity(nameCity)
        }

        emit(Resource(EventStatus.CITY_ADDED,
            dataBaseHelper.createWeatherCity(newWeatherCity)))
    }.exceptionCreateWeather()

    fun updateWeatherCity(weatherCity: WeatherCity): Flow<Resource<WeatherCity>> = flow {
        val newWeatherCity = withContext(Dispatchers.IO) {
            weatherData.getUpdateWeatherCity(weatherCity)
        }

        emit(Resource(EventStatus.CITY_WEATHER_DATA_UPDATED,
            dataBaseHelper.updateWeatherCity(newWeatherCity)))
    }.exceptionUpdateWeather(weatherCity)

    fun updateWeatherCities(): Flow<Resource<ArrayList<WeatherCity>>> = flow {
        val weatherCityList = withContext(Dispatchers.IO) {
            weatherData.getUpdatedWeatherCityList(dataBaseHelper.getWeatherCities())
        }

        if (weatherCityList.isNotEmpty()) {
            emit(Resource(EventStatus.CITY_WEATHER_DATA_UPDATED,
                dataBaseHelper.updateRecyclerCitiesWeather(weatherCityList)))

        } else emit(Resource(EventStatus.IS_NOT_REFRESHING,
            dataBaseHelper.getWeatherCities()))
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
            : Flow<Resource<WeatherCity>> = flow {

        val newWeatherCity: WeatherCity = withContext(Dispatchers.IO) {
            weatherData.getWeatherCityByCoordinate(coordinateLat, coordinateLon)
        }

        emit(Resource(EventStatus.CURRENT_LOCATION_RECEIVED,
            changeWeatherCurrentLocation(newWeatherCity)))
    }.exceptionCreateWeatherLocation()

    fun createWeatherCurrentLocation(nameCity: String): Flow<Resource<WeatherCity>> = flow {
        val newWeatherCity: WeatherCity = withContext(Dispatchers.IO) {
            weatherData.getWeatherCity(nameCity)
        }

        emit(Resource(EventStatus.CURRENT_LOCATION_RECEIVED,
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
    fun getWeatherCityByDeepLinkData(nameCity: String): Flow<Resource<WeatherCity>> = flow {
            val newWeatherCity = withContext(Dispatchers.IO) {
                weatherData.getWeatherCity(nameCity)
            }

            val oldWeatherCity = getWeatherCityByName(newWeatherCity.nameCity)

            if (oldWeatherCity != null) {
                emit(Resource(EventStatus.CITY_WEATHER_DATA_RECEIVED,
                    dataBaseHelper.updateWeatherCity(
                    weatherData.broadcastingImmutableData(oldWeatherCity, newWeatherCity))))
            } else {
                emit(Resource(EventStatus.CITY_WEATHER_DATA_RECEIVED, newWeatherCity))
            }
    }.exceptionGettingWeatherByDeepLink()

    /** Precipitation */
    fun getTileData(layer: String, zoom: Int, x: Int, y: Int)
        : Flow<Resource<TileData>> = flow {

        val newTileData = withContext(Dispatchers.IO) {
            weatherData.getTileData(layer, zoom, x, y)
        }

        emit(Resource(EventStatus.PRECIPITATION_TILE_ACCEPTED, newTileData))
    }.exceptionGettingPrecipitation()
}