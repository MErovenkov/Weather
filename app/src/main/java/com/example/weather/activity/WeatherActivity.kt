package com.example.weather.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.R
import com.example.weather.dao.OrmLiteHelper
import com.example.weather.model.WeatherCity
import com.example.weather.utils.api.WeatherApi
import com.example.weather.utils.CheckStatus
import com.example.weather.view.recycler.GenericAdapter
import com.example.weather.view.recycler.SwipeToDeleteCallback
import com.example.weather.view.recycler.toast.ShowToast
import kotlinx.coroutines.*
import java.io.FileNotFoundException
import java.net.ConnectException
import javax.net.ssl.SSLException

class WeatherActivity : AppCompatActivity() {
    private lateinit var dataBaseHelper: OrmLiteHelper
    private lateinit var adapterRecyclerView: GenericAdapter<WeatherCity>
    private lateinit var addingNewCity: EditText
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var weatherApi: WeatherApi
    private var weatherCityList: ArrayList<WeatherCity> = ArrayList()

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        addingNewCity = findViewById(R.id.aw_adding_new_city)

        weatherApi = WeatherApi(this)

        dataBaseHelper = OrmLiteHelper(this)
        weatherCityList = ArrayList(dataBaseHelper.getWeatherCityDao().queryForAll())
        initRecyclerView()

        if (!weatherCityList.equals(null)) {
            adapterRecyclerView.update(weatherCityList)

            if (CheckStatus.isNetworkAvailable(this)) {
                GlobalScope.launch(Dispatchers.Main) {
                    updateRecyclerViewValidData()
                }
            }
        }

        swipeRefreshLayout = findViewById(R.id.aw_swipe_fresh)
        swipeRefreshLayout.setOnRefreshListener {
            if (CheckStatus.isNetworkAvailable(this)) {
                GlobalScope.launch(Dispatchers.Main) {
                    weatherCityList = ArrayList(adapterRecyclerView.getItemList())
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
            weatherCityList =
                withContext(Dispatchers.IO) { weatherApi.getUpdatedWeatherCityList(weatherCityList) }
            adapterRecyclerView.update(weatherCityList)
        } catch (e: ConcurrentModificationException) {
            Log.w(e.toString(), Thread.currentThread().stackTrace[2].toString())
            weatherCityList = ArrayList(adapterRecyclerView.getItemList())
        } catch (e: ConnectException) {
            Log.w(e.toString(), Thread.currentThread().stackTrace[2].toString())
            weatherCityList = ArrayList(adapterRecyclerView.getItemList())
        } catch (e: SSLException) {
            Log.w(e.toString(), Thread.currentThread().stackTrace[2].toString())
            weatherCityList = ArrayList(adapterRecyclerView.getItemList())
        }
    }

    private fun initRecyclerView() {
        adapterRecyclerView = object : GenericAdapter<WeatherCity>(){}
        @Suppress("UNUSED_PARAMETER") val recyclerView =
            findViewById<RecyclerView>(R.id.aw_recycler_view).apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(this@WeatherActivity)
            adapter = adapterRecyclerView
        }
        val touchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapterRecyclerView))
        touchHelper.attachToRecyclerView(recyclerView)
    }

    fun createNewCity(@Suppress("UNUSED_PARAMETER") view: View){
        val nameCity = addingNewCity.text.toString()
        weatherCityList = ArrayList(adapterRecyclerView.getItemList())

        if (CheckStatus.isNetworkAvailable(this)) {
            if (nameCity.trim().isNotEmpty()) {
                addingNewCity.text.clear()
                addingNewCity.isCursorVisible = false

                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        val newWeatherCity =
                            withContext(Dispatchers.IO) { weatherApi.getWeatherCity(nameCity) }

                        if (weatherCityList.none { oldWeatherCity ->
                                oldWeatherCity.nameCity == newWeatherCity.nameCity
                            }) {
                            adapterRecyclerView.addItem(newWeatherCity)
                        } else ShowToast.getToast(
                            this@WeatherActivity,
                            this@WeatherActivity.resources.getString(R.string.city_exist)
                        )
                    } catch (e: FileNotFoundException) {
                        ShowToast.getToast(
                            this@WeatherActivity,
                            this@WeatherActivity.resources.getString(R.string.city_not_found)
                        )
                        Log.w(
                            "$e nameCity: $nameCity", Thread.currentThread()
                                .stackTrace[2].toString()
                        )
                    }
                }
            }
        } else ShowToast.getToast(this@WeatherActivity,
            this.resources.getString(R.string.no_internet_access))
    }

    fun openWeatherDetailed(view: View) {
        val nameCity: TextView = view.findViewById(R.id.w_rec_city_name)
        val intent = Intent(this, DetailedWeatherActivity::class.java);
        intent.putExtra("nameCity", nameCity.text)
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        dataBaseHelper.changesAllData(ArrayList(adapterRecyclerView.getItemList()))
        super.onPause()
    }

    override fun onDestroy() {
        dataBaseHelper.close()
        super.onDestroy()
    }
}