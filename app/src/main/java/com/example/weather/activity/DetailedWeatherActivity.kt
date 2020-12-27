package com.example.weather.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.databinding.ActivityDetailedWeatherBinding
import com.example.weather.model.WeatherFuture
import com.example.weather.utils.CheckStatusNetwork
import com.example.weather.utils.extensions.getActivityComponent
import com.example.weather.utils.extensions.isNetworkAvailable
import com.example.weather.utils.extensions.showNoInternetAccess
import com.example.weather.utils.extensions.showToast
import com.example.weather.view.recycler.GenericAdapter
import com.example.weather.viewmodel.DetailedWeatherViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailedWeatherActivity: AppCompatActivity()  {

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
        initSwipeRefreshLayout()

        isCurrentLocation = intent.getBooleanExtra(IS_CURRENT_LOCATION_KEY, false)

        detailedWeatherViewModel.initLiveData(intent.getStringExtra(CITY_NAME_KEY).toString(),
            intent.getBooleanExtra(IS_CURRENT_LOCATION_KEY, false))

        viewModelCollector()
    }

    private fun initRecyclerView() {
        adapterRecyclerView = object : GenericAdapter<WeatherFuture>() {}
        binding.adwRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context)
            adapter = adapterRecyclerView
        }
    }

    private fun initSwipeRefreshLayout() {
        swipeRefreshLayout = binding.adwSwipeFresh
        swipeRefreshLayout.setOnRefreshListener {
            if (CheckStatusNetwork.isNetworkAvailable()) {
                detailedWeatherViewModel.updateWeatherCity(isCurrentLocation)
            } else {
                showNoInternetAccess()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun viewModelCollector() {
        lifecycleScope.launch {
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

                resource.getEvent()?.let { event ->
                    this@DetailedWeatherActivity.showToast(event)
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}