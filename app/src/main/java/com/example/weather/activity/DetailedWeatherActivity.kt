package com.example.weather.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.R
import com.example.weather.dao.OrmLiteHelper
import com.example.weather.model.WeatherCity
import com.example.weather.model.WeatherFuture
import com.example.weather.utils.CheckStatus
import com.example.weather.utils.api.WeatherApi
import com.example.weather.view.recycler.GenericAdapter
import com.example.weather.view.recycler.toast.ShowToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import javax.net.ssl.SSLException

class DetailedWeatherActivity: AppCompatActivity()  {
    private lateinit var dataBaseHelper: OrmLiteHelper

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var adapterRecyclerView: GenericAdapter<WeatherFuture>

    private lateinit var nameCityText: TextView
    private lateinit var temperature: TextView
    private lateinit var iconWeatherCurrent: ImageView

    private lateinit var weatherApi: WeatherApi

    private lateinit var gotNameCity: String
    private lateinit var weatherCity: WeatherCity
    private lateinit var weatherFutureList: ArrayList<WeatherFuture>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_weather)

        weatherApi =  WeatherApi(this)

        dataBaseHelper = OrmLiteHelper(this)
        initRecyclerView()

        gotNameCity = intent.extras?.getString("nameCity").toString()
        weatherCity = dataBaseHelper.getWeatherCityDao().getWeatherCityByName(gotNameCity)

        nameCityText = findViewById(R.id.adw_city_name)
        temperature = findViewById(R.id.adw_current_temperature)
        iconWeatherCurrent = findViewById(R.id.adw_icon_current_weather)

        nameCityText.text = weatherCity.nameCity
        temperature.text = weatherCity.weatherCurrent.temperature
        iconWeatherCurrent.setImageResource(resources
            .getIdentifier(weatherCity.weatherCurrent.nameIconWeather,
                "drawable", packageName))

        weatherFutureList = ArrayList(weatherCity.weatherFutureList)

        adapterRecyclerView.update(weatherFutureList)

        swipeRefreshLayout = findViewById(R.id.adw_swipe_fresh)
        swipeRefreshLayout.setOnRefreshListener {
            if (CheckStatus.isNetworkAvailable(this)) {
                GlobalScope.launch(Dispatchers.Main) {
                    updateRecyclerViewValidData()
                    swipeRefreshLayout.isRefreshing = false
                }
            } else {
                ShowToast.getToast(this,
                    this.resources.getString(R.string.no_internet_access))
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private suspend fun updateRecyclerViewValidData(){
        try {
            weatherCity =
                withContext(Dispatchers.IO) { weatherApi.getWeatherCity(weatherCity.nameCity) }
            adapterRecyclerView.update(ArrayList(weatherCity.weatherFutureList))
            nameCityText.text = weatherCity.nameCity
            temperature.text = weatherCity.weatherCurrent.temperature
            iconWeatherCurrent.setImageResource(resources.getIdentifier(
                        weatherCity.weatherCurrent.nameIconWeather, "drawable",
                        packageName))
        } catch (e: ConnectException) {
            Log.w(e.toString(), Thread.currentThread().stackTrace[2].toString())
            weatherCity.weatherFutureList = ArrayList(adapterRecyclerView.getItemList())
        } catch (e: SSLException) {
            Log.w(e.toString(), Thread.currentThread().stackTrace[2].toString())
            weatherCity.weatherFutureList = ArrayList(adapterRecyclerView.getItemList())
        }
    }

    private fun initRecyclerView() {
        adapterRecyclerView = object : GenericAdapter<WeatherFuture>() {}
        @Suppress("UNUSED_VARIABLE") val recyclerView =
            findViewById<RecyclerView>(R.id.adw_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@DetailedWeatherActivity)
            adapter = adapterRecyclerView
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, WeatherActivity::class.java);
        startActivity(intent)
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
}