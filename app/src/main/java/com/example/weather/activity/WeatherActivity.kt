package com.example.weather.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.R
import com.example.weather.databinding.ActivityWeatherBinding
import com.example.weather.databinding.WRecWeatherCurrentBinding
import com.example.weather.di.MyApplication
import com.example.weather.model.WeatherCity
import com.example.weather.utils.CheckStatusNetwork
import com.example.weather.view.recycler.GenericAdapter
import com.example.weather.view.recycler.SwipeToDeleteCallback
import com.example.weather.view.toast.ShowToast
import com.example.weather.viewmodel.WeatherViewModel
import java.net.ConnectException
import java.sql.SQLException
import javax.inject.Inject
import javax.net.ssl.SSLException

class WeatherActivity: AppCompatActivity() {
    @Inject
    lateinit var weatherViewModel: WeatherViewModel

    private lateinit var binding: ActivityWeatherBinding
    private lateinit var adapterRecyclerView: GenericAdapter<WeatherCity>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyApplication).appComponent.activityComponent().create(this)
            .inject(this)

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        weatherViewModel.getWeatherCityList().observe(this) {
            adapterRecyclerView.update(it)
        }

        swipeRefreshLayout = binding.awSwipeFresh
        swipeRefreshLayout.setOnRefreshListener {
            if (CheckStatusNetwork.isNetworkAvailable()) {
                updateWeatherData()
                swipeRefreshLayout.isRefreshing = false
            } else {
                ShowToast.getToast(application.resources.getString(R.string.no_internet_access))
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun initRecyclerView() {
        adapterRecyclerView = object : GenericAdapter<WeatherCity>(){
            override fun <T> itemDismiss(data: T) {
                weatherViewModel.deleteWeatherCity(data as WeatherCity)
            }
        }
        val recyclerView = binding.awRecyclerView.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(this.context)
            adapter = adapterRecyclerView
        }
        val touchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapterRecyclerView))
        touchHelper.attachToRecyclerView(recyclerView)
    }


    fun createNewCity(@Suppress("UNUSED_PARAMETER") view: View) {
        val addingNewCity = binding.awAddingNewCity
        val nameCity = addingNewCity.text.toString()

        if (CheckStatusNetwork.isNetworkAvailable()) {
            if (nameCity.trim().isNotEmpty()) {
                addingNewCity.text.clear()
                addingNewCity.isCursorVisible = false

                try {
                    weatherViewModel.createWeatherData(nameCity)
                    ShowToast.getToast(this.resources.getString(R.string.city_added))
                } catch (e: NullPointerException) {
                    Log.w("$e nameCity: $nameCity", e.stackTraceToString())
                    ShowToast.getToast(this.getString(R.string.city_not_found))
                } catch (e: SQLException) {
                    Log.w("$e nameCity: $nameCity", e.stackTraceToString())
                    ShowToast.getToast(this.getString(R.string.city_exist))
                }
            } else ShowToast.getToast(application.getString(R.string.city_name_not_empty))
        } else ShowToast.getToast(application.resources.getString(R.string.no_internet_access))
    }

    private fun updateWeatherData() {
        try {
            weatherViewModel.updateWeatherData()
            ShowToast.getToast(this.getString(R.string.city_weather_data_updated))
        } catch (e: ConcurrentModificationException) {
            Log.w(e.toString(), e.stackTraceToString())
            ShowToast.getToast(this.getString(R.string.city_weather_update_failed))
        } catch (e: ConnectException) {
            Log.w(e.toString(),  e.stackTraceToString())
            ShowToast.getToast(this.getString(R.string.lost_internet_access))
        } catch (e: SSLException) {
            Log.w(e.toString(),  e.stackTraceToString())
            ShowToast.getToast(this.getString(R.string.city_weather_update_failed))
        }
    }

    fun openWeatherDetailed(view: View) {
        val bindings: WRecWeatherCurrentBinding = WRecWeatherCurrentBinding.bind(view)

        startActivity(
            DetailedWeatherActivity.createIntent(this, bindings.wRecCityName.text as String)
        )
    }

    override fun onResume() {
        super.onResume()
        weatherViewModel.updateRequestDB()
    }
}