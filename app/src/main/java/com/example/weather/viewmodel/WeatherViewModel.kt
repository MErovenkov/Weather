package com.example.weather.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.weather.R
import com.example.weather.api.WeatherData
import com.example.weather.dao.OrmLiteHelper
import com.example.weather.model.WeatherCity
import com.example.weather.view.toast.ShowToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.sql.SQLException
import javax.net.ssl.SSLException

class WeatherViewModel (application: Application,
                        private val dataBaseHelper: OrmLiteHelper,
                        private val weatherData: WeatherData): AndroidViewModel(application) {
    private var weatherCityList : MutableLiveData<ArrayList<WeatherCity>> = MutableLiveData()

    init {
        updateRequestDB()
    }

    fun getWeatherCityList() = weatherCityList

    fun createWeatherData(nameCity: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val newWeatherCity =
                    withContext(Dispatchers.IO) { weatherData.getWeatherCity(nameCity) }

                dataBaseHelper.createWeatherCity(newWeatherCity)
                updateRequestDB()
                ShowToast.getToast((getApplication()
                        as Context).resources.getString(R.string.city_added))
            } catch (e: NullPointerException) {
                Log.w("$e nameCity: $nameCity", e.stackTraceToString())
                ShowToast.getToast((getApplication()
                        as Context).getString(R.string.city_not_found))
            } catch (e: SQLException) {
                Log.w("$e nameCity: $nameCity", e.stackTraceToString())
                ShowToast.getToast((getApplication()
                        as Context).getString(R.string.city_exist))
            }
        }
    }

    fun updateWeatherData() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                weatherCityList.value = withContext(Dispatchers.IO) {
                    weatherData.getUpdatedWeatherCityList(weatherCityList.value
                            as ArrayList<WeatherCity>)
                }
                dataBaseHelper.updateAllCitiesWeather(weatherCityList.value
                        as ArrayList<WeatherCity>)

                ShowToast.getToast((getApplication()
                        as Context).getString(R.string.city_weather_data_updated))
            } catch (e: ConcurrentModificationException) {
                Log.w(e.toString(), e.stackTraceToString())
                ShowToast.getToast((getApplication()
                        as Context).getString(R.string.city_weather_update_failed))
            } catch (e: ConnectException) {
                Log.w(e.toString(),  e.stackTraceToString())
                ShowToast.getToast((getApplication()
                        as Context).getString(R.string.lost_internet_access))
            } catch (e: SSLException) {
                Log.w(e.toString(),  e.stackTraceToString())
                ShowToast.getToast((getApplication()
                        as Context).getString(R.string.city_weather_update_failed))
            }
        }
    }

    fun updateRequestDB() {
        weatherCityList.value = dataBaseHelper.getWeatherCityDao().queryForAll() as ArrayList<WeatherCity>?
    }

    fun deleteWeatherCity(weatherCity: WeatherCity) {
        dataBaseHelper.deletedWeatherCity(weatherCity)
    }
}