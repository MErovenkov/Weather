package com.example.weather.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.R
import com.example.weather.dao.DBHelper
import com.example.weather.dao.OrmLiteHelper
import com.example.weather.databinding.ActivityWeatherBinding
import com.example.weather.databinding.WRecWeatherCurrentBinding
import com.example.weather.model.WeatherCity
import com.example.weather.utils.WeatherData
import com.example.weather.utils.CheckStatus
import com.example.weather.utils.WeatherDataHelper
import com.example.weather.view.recycler.GenericAdapter
import com.example.weather.view.recycler.SwipeToDeleteCallback
import com.example.weather.view.toast.ShowToast
import kotlinx.coroutines.*
import java.net.ConnectException
import javax.net.ssl.SSLException

class WeatherActivity : AppCompatActivity() {
    private lateinit var dataBaseHelper: OrmLiteHelper

    private lateinit var binding: ActivityWeatherBinding
    private lateinit var addingNewCity: EditText
    private lateinit var adapterRecyclerView: GenericAdapter<WeatherCity>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var weatherData: WeatherData
    private var weatherCityList: ArrayList<WeatherCity> = ArrayList()

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DBHelper.setContext(applicationContext)
        dataBaseHelper = DBHelper.getDB()

        CheckStatus.setContext(applicationContext)
        ShowToast.setContext(applicationContext)

        WeatherDataHelper.setContext(applicationContext)
        weatherData = WeatherDataHelper.getWeatherData()

        addingNewCity = binding.awAddingNewCity

        weatherCityList = ArrayList(dataBaseHelper.getWeatherCityDao().queryForAll())
        initRecyclerView()

        if (weatherCityList.isNotEmpty()) {
            adapterRecyclerView.update(weatherCityList)

            if (CheckStatus.isNetworkAvailable()) {
                GlobalScope.launch(Dispatchers.Main) {
                    updateRecyclerViewValidData()
                }
            }
        }

        swipeRefreshLayout = binding.awSwipeFresh
        swipeRefreshLayout.setOnRefreshListener {
            if (CheckStatus.isNetworkAvailable()) {
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
            weatherCityList =
                withContext(Dispatchers.IO) { weatherData
                        .getUpdatedWeatherCityList(ArrayList(adapterRecyclerView.getItemList())) }
            adapterRecyclerView.update(weatherCityList)
            dataBaseHelper.changesAllData(weatherCityList)
        } catch (e: ConcurrentModificationException) {
            ShowToast.getToast(applicationContext.getString(R.string.city_weather_update_failed))
            Log.w(e.toString(), Thread.currentThread().stackTrace[2].toString())
        } catch (e: ConnectException) {
            ShowToast.getToast(applicationContext.getString(R.string.lost_internet_access))
            Log.w(e.toString(), Thread.currentThread().stackTrace[2].toString())
        } catch (e: SSLException) {
            ShowToast.getToast(applicationContext.getString(R.string.city_weather_update_failed))
            Log.w(e.toString(), Thread.currentThread().stackTrace[2].toString())
        }
    }

    private fun initRecyclerView() {
        adapterRecyclerView = object : GenericAdapter<WeatherCity>(){}
        @Suppress("UNUSED_PARAMETER") val recyclerView = binding.awRecyclerView.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(this.context)
            adapter = adapterRecyclerView
        }
        val touchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapterRecyclerView))
        touchHelper.attachToRecyclerView(recyclerView)
    }

    fun createNewCity(@Suppress("UNUSED_PARAMETER") view: View){
        val nameCity = addingNewCity.text.toString()
        weatherCityList = ArrayList(adapterRecyclerView.getItemList())

        if (CheckStatus.isNetworkAvailable()) {
            if (nameCity.trim().isNotEmpty()) {
                addingNewCity.text.clear()
                addingNewCity.isCursorVisible = false

                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        val newWeatherCity =
                            withContext(Dispatchers.IO) { weatherData.getWeatherCity(nameCity) }

                        if (weatherCityList.none { oldWeatherCity ->
                                oldWeatherCity.nameCity == newWeatherCity.nameCity }) {
                            adapterRecyclerView.addItem(newWeatherCity)
                            ShowToast.getToast(applicationContext.resources.getString(R.string.city_added))
                        } else ShowToast.getToast(applicationContext.getString(R.string.city_exist))
                    } catch (e: NullPointerException) {
                        ShowToast.getToast(applicationContext.getString(R.string.city_not_found))
                        Log.w("$e nameCity: $nameCity", Thread.currentThread()
                                .stackTrace[2].toString())
                    }
                }
            }
        }
    }

    fun openWeatherDetailed(view: View) {
        val bindings: WRecWeatherCurrentBinding = WRecWeatherCurrentBinding.bind(view)

        startActivity(DetailedWeatherActivity.createIntent(applicationContext)
            .putExtra("nameCity", bindings.wRecCityName.text))
    }

    override fun onResume() {
        super.onResume()
        adapterRecyclerView.update(ArrayList(dataBaseHelper.getWeatherCityDao().queryForAll()))
    }

    override fun onPause() {
        super.onPause()
        dataBaseHelper.changesAllData(ArrayList(adapterRecyclerView.getItemList()))
    }

    override fun onDestroy() {
        super.onDestroy()
        DBHelper.releaseDB()
    }
}