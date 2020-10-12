package com.example.weather.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.R
import com.example.weather.dao.OrmLiteHelper
import com.example.weather.databinding.ActivityDetailedWeatherBinding
import com.example.weather.model.WeatherCity
import com.example.weather.model.WeatherFuture
import com.example.weather.utils.CheckStatus
import com.example.weather.utils.WeatherData
import com.example.weather.view.recycler.GenericAdapter
import com.example.weather.view.toast.ShowToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import javax.net.ssl.SSLException

class DetailedWeatherActivity: AppCompatActivity()  {
    private lateinit var dataBaseHelper: OrmLiteHelper

    private lateinit var binding: ActivityDetailedWeatherBinding
    private lateinit var nameCityText: TextView
    private lateinit var temperature: TextView
    private lateinit var iconWeatherCurrent: ImageView
    private lateinit var adapterRecyclerView: GenericAdapter<WeatherFuture>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var weatherData: WeatherData
    private lateinit var gotNameCity: String
    private lateinit var weatherCity: WeatherCity
    private lateinit var weatherFutureList: ArrayList<WeatherFuture>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weatherData =  WeatherData(this)

        dataBaseHelper = OrmLiteHelper(this)
        initRecyclerView()

        gotNameCity = intent.getStringExtra("nameCity").toString()
        weatherCity = dataBaseHelper.getWeatherCityDao().getWeatherCityByName(gotNameCity)

        nameCityText = binding.adwCityName
        temperature = binding.adwCurrentTemperature
        iconWeatherCurrent = binding.adwIconCurrentWeather

        nameCityText.text = weatherCity.nameCity
        temperature.text = weatherCity.weatherCurrent.temperature
        iconWeatherCurrent.setImageResource(resources
            .getIdentifier("ic_current_${weatherCity.weatherCurrent.nameIconWeather}",
                "drawable", packageName))

        weatherFutureList = ArrayList(weatherCity.weatherFutureList)

        adapterRecyclerView.update(weatherFutureList)

        swipeRefreshLayout = binding.adwSwipeFresh
        swipeRefreshLayout.setOnRefreshListener {
            if (CheckStatus.isNetworkAvailable(this)) {
                GlobalScope.launch(Dispatchers.Main) {
                    updateRecyclerViewValidData()
                    swipeRefreshLayout.isRefreshing = false
                }
            } else {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private suspend fun updateRecyclerViewValidData(){
        try {
            weatherCity =
                withContext(Dispatchers.IO) { weatherData.getWeatherCity(weatherCity.nameCity) }
            adapterRecyclerView.update(ArrayList(weatherCity.weatherFutureList))
            nameCityText.text = weatherCity.nameCity
            temperature.text = weatherCity.weatherCurrent.temperature
            iconWeatherCurrent.setImageResource(resources.getIdentifier(
                "ic_current_${weatherCity.weatherCurrent.nameIconWeather}", "drawable",
                        packageName))

        } catch (e: ConnectException) {
            ShowToast.getToast(this,
                this.resources.getString(R.string.lost_internet_access))
            Log.w(e.toString(), Thread.currentThread().stackTrace[2].toString())
        } catch (e: SSLException) {
            ShowToast.getToast(this,
                this.resources.getString(R.string.city_weather_update_failed))
            Log.w(e.toString(), Thread.currentThread().stackTrace[2].toString())
        }
    }

    private fun initRecyclerView() {
        adapterRecyclerView = object : GenericAdapter<WeatherFuture>() {}
        @Suppress("UNUSED_VARIABLE") val recyclerView = binding.adwRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@DetailedWeatherActivity)
            adapter = adapterRecyclerView
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onPause() {
        dataBaseHelper.updateCity(weatherCity)
        super.onPause()
    }

    override fun onDestroy() {
        dataBaseHelper.close()
        super.onDestroy()
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, DetailedWeatherActivity::class.java)
        }
    }
}