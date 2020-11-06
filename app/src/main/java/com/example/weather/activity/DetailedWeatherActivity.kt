package com.example.weather.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.R
import com.example.weather.databinding.ActivityDetailedWeatherBinding
import com.example.weather.model.WeatherFuture
import com.example.weather.utils.CheckStatusNetwork
import com.example.weather.utils.getActivityComponent
import com.example.weather.view.recycler.GenericAdapter
import com.example.weather.viewmodel.DetailedWeatherViewModel
import javax.inject.Inject

class DetailedWeatherActivity: AppCompatActivity()  {
    @Inject
    lateinit var detailedWeatherViewModel: DetailedWeatherViewModel

    private lateinit var binding: ActivityDetailedWeatherBinding
    private lateinit var adapterRecyclerView: GenericAdapter<WeatherFuture>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent(this).inject(this)

        binding = ActivityDetailedWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        detailedWeatherViewModel.initLiveData(intent.getStringExtra("cityName").toString())
        detailedWeatherViewModel.apply {
            getWeatherCity().observe(this@DetailedWeatherActivity) {
                binding.apply {
                    adwCityName.text = it.nameCity
                    adwCurrentTemperature.text = it.weatherCurrent.temperature
                    adwIconCurrentWeather.setImageResource(
                        resources
                            .getIdentifier(
                                "ic_current_w${it.weatherCurrent.nameIconWeather}",
                                "drawable", packageName
                            )
                    )
                }
                adapterRecyclerView.update(ArrayList(it.weatherFutureList))
            }

            getEvent().observe(this@DetailedWeatherActivity) {
                if (it != null) {
                    Toast.makeText(this@DetailedWeatherActivity,
                        this@DetailedWeatherActivity.getString(it), Toast.LENGTH_SHORT).show()
                }
            }
        }

        swipeRefreshLayout = binding.adwSwipeFresh
        swipeRefreshLayout.setOnRefreshListener {
            if (CheckStatusNetwork.isNetworkAvailable()) {
                detailedWeatherViewModel.updateWeatherData()
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

        fun createIntent(context: Context, cityName: String): Intent  {
            return Intent(context, DetailedWeatherActivity::class.java).apply {
                putExtra(CITY_NAME_KEY, cityName)
            }
        }
    }
}