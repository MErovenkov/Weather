package com.example.weather.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.weather.R
import com.example.weather.dao.OrmLiteHelper
import com.example.weather.model.WeatherCity
import com.example.weather.api.WeatherData
import com.example.weather.view.toast.ShowToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import javax.net.ssl.SSLException

class DetailedWeatherViewModel (application: Application,
                                private val dataBaseHelper: OrmLiteHelper,
                                private val weatherData: WeatherData
    ) : AndroidViewModel(application) {

    private var weatherCity : MutableLiveData<WeatherCity> = MutableLiveData()

    fun initLiveData(nameCity: String) {
        weatherCity.value = dataBaseHelper.getWeatherCityDao().getWeatherCityByName(nameCity)
    }

    fun getWeatherCity() = weatherCity

    fun updateWeatherData() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                weatherCity.value = withContext(Dispatchers.IO) {
                        weatherData.getUpdateWeatherCity(weatherCity.value!!)
                }
                dataBaseHelper.updateWeatherCity(weatherCity.value!!)
                ShowToast.getToast((getApplication()
                        as Context).getString(R.string.city_weather_data_updated))
            } catch (e: ConnectException) {
                Log.w(e.toString(), e.stackTraceToString())
                ShowToast.getToast((getApplication()
                        as Context).getString(R.string.lost_internet_access))
            } catch (e: SSLException) {
                Log.w(e.toString(), e.stackTraceToString())
                ShowToast.getToast((getApplication()
                        as Context).getString(R.string.city_weather_update_failed))
            }
        }
    }
}