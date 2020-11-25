package com.example.weather.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.R
import com.example.weather.databinding.ActivityDetailedWeatherBinding
import com.example.weather.model.WeatherFuture
import com.example.weather.utils.CheckStatusNetwork
import com.example.weather.utils.extensions.getActivityComponent
import com.example.weather.view.recycler.GenericAdapter
import com.example.weather.viewmodel.DetailedWeatherViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailedWeatherActivity: AppCompatActivity()  {
    @Inject
    lateinit var detailedWeatherViewModel: DetailedWeatherViewModel

    private lateinit var binding: ActivityDetailedWeatherBinding
    private lateinit var adapterRecyclerView: GenericAdapter<WeatherFuture>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var isCurrentLocation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent(this).inject(this)

        binding = ActivityDetailedWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        isCurrentLocation = intent.getBooleanExtra(IS_CURRENT_LOCATION_KEY, false)

        lifecycleScope.launch {
            detailedWeatherViewModel.initLiveData(intent.getStringExtra(CITY_NAME_KEY).toString(),
                intent.getBooleanExtra(IS_CURRENT_LOCATION_KEY, false))

            detailedWeatherViewModel.getResource().collect { resource ->
                resource.getData()?.let { weatherCity ->
                    binding.apply {
                        adwCityName.text = weatherCity.nameCity
                        adwCurrentTemperature.text = weatherCity.weatherCurrent.temperature
                        adwIconCurrentWeather.setImageResource(
                            resources.getIdentifier(
                                "ic_current_w${weatherCity.weatherCurrent.nameIconWeather}",
                                "drawable", packageName
                            )
                        )
                    }
                    adapterRecyclerView
                        .update(ArrayList(weatherCity.weatherFutureList))
                }

                resource.getEvent()?.let {
                    event -> Toast.makeText(this@DetailedWeatherActivity,
                    this@DetailedWeatherActivity.getString(event), Toast.LENGTH_SHORT).show()
                }
            }
        }

        swipeRefreshLayout = binding.adwSwipeFresh
        swipeRefreshLayout.setOnRefreshListener {
            if (CheckStatusNetwork.isNetworkAvailable()) {
                detailedWeatherViewModel.updateWeatherData(isCurrentLocation)
                swipeRefreshLayout.isRefreshing = false
            } else {
                Toast.makeText(this,
                    this.getString(R.string.no_internet_access),
                    Toast.LENGTH_SHORT).show()
                swipeRefreshLayout.isRefreshing = false
            }
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
        private const val IS_CURRENT_LOCATION_KEY = "isCurrentLocation"

        fun createIntent(context: Context, cityName: String, isCurrentLocation: Boolean): Intent  {
            return Intent(context, DetailedWeatherActivity::class.java).apply {
                putExtra(CITY_NAME_KEY, cityName)
                putExtra(IS_CURRENT_LOCATION_KEY, isCurrentLocation)
            }
        }
    }
}