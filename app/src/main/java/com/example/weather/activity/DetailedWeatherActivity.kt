package com.example.weather.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.R
import com.example.weather.databinding.ActivityDetailedWeatherBinding
import com.example.weather.di.MyApplication
import com.example.weather.model.WeatherFuture
import com.example.weather.utils.CheckStatusNetwork
import com.example.weather.view.recycler.GenericAdapter
import com.example.weather.view.toast.ShowToast
import com.example.weather.viewmodel.DetailedWeatherViewModel
import java.net.ConnectException
import javax.inject.Inject
import javax.net.ssl.SSLException

class DetailedWeatherActivity: AppCompatActivity()  {
    @Inject
    lateinit var detailedWeatherViewModel: DetailedWeatherViewModel

    private lateinit var binding: ActivityDetailedWeatherBinding
    private lateinit var adapterRecyclerView: GenericAdapter<WeatherFuture>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyApplication).appComponent.activityComponent()
            .create(this).inject(this)

        binding = ActivityDetailedWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        detailedWeatherViewModel.initLiveData(intent.getStringExtra("cityName").toString())
        detailedWeatherViewModel.getWeatherCity().observe(this) {
            binding.apply {
                adwCityName.text = it.nameCity
                adwCurrentTemperature.text = it.weatherCurrent.temperature
                adwIconCurrentWeather.setImageResource(resources
                    .getIdentifier("ic_current_w${it.weatherCurrent.nameIconWeather}",
                        "drawable", packageName))
            }
            adapterRecyclerView.update(ArrayList(it.weatherFutureList))
        }

        swipeRefreshLayout = binding.adwSwipeFresh
        swipeRefreshLayout.setOnRefreshListener {
            if (CheckStatusNetwork.isNetworkAvailable()) {
                updateWeatherData()
                swipeRefreshLayout.isRefreshing = false
            } else {
                ShowToast.getToast(this.resources.getString(R.string.no_internet_access))
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun updateWeatherData() {
        try {
            detailedWeatherViewModel.updateWeatherData()
            ShowToast.getToast(this.getString(R.string.city_weather_data_updated))
        } catch (e: ConnectException) {
            Log.w(e.toString(), e.stackTraceToString())
            ShowToast.getToast(this.getString(R.string.lost_internet_access))
        } catch (e: SSLException) {
            Log.w(e.toString(), e.stackTraceToString())
            ShowToast.getToast(this.getString(R.string.city_weather_update_failed))
        }
    }

    private fun initRecyclerView() {
        adapterRecyclerView = object : GenericAdapter<WeatherFuture>() {}
        binding.adwRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context)
            adapter = adapterRecyclerView
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        private const val CITY_NAME_KEY = "cityName"

        fun createIntent(context: Context, cityName: String): Intent  {
            return Intent(context, DetailedWeatherActivity::class.java).apply {
                putExtra(CITY_NAME_KEY, cityName)
            }
        }
    }
}