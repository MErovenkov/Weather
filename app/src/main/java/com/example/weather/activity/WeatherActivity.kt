package com.example.weather.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.R
import com.example.weather.databinding.ActivityWeatherBinding
import com.example.weather.databinding.WRecWeatherCurrentBinding
import com.example.weather.model.WeatherCity
import com.example.weather.utils.CheckStatusNetwork
import com.example.weather.utils.extension.getActivityComponent
import com.example.weather.location.LocationService
import com.example.weather.view.recycler.GenericAdapter
import com.example.weather.view.recycler.SwipeToDeleteCallback
import com.example.weather.viewmodel.WeatherViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class WeatherActivity: AppCompatActivity() {
    @Inject
    lateinit var weatherViewModel: WeatherViewModel
    @Inject
    lateinit var locationService: LocationService

    private lateinit var binding: ActivityWeatherBinding
    private lateinit var adapterRecyclerView: GenericAdapter<WeatherCity>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var weatherCurrentLocation: WeatherCity? = null
    private var isLocationInfoUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent(this).inject(this)

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        viewModelCollector()
        locationServiceCollector()

        swipeRefreshLayout = binding.awSwipeFresh
        swipeRefreshLayout.setOnRefreshListener {
            if (CheckStatusNetwork.isNetworkAvailable()) {
                weatherViewModel.updateAllCitiesWeather()
                swipeRefreshLayout.isRefreshing = false
            } else {
                Toast.makeText(
                    this,
                    this.getString(R.string.no_internet_access),
                    Toast.LENGTH_SHORT
                ).show()
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

    private fun viewModelCollector() {
        lifecycleScope.apply {
            launch {
                weatherViewModel.getResource().collect { resource ->
                    resource.getData()?.let { weatherCities ->
                        adapterRecyclerView.update(
                            weatherCities
                                .filter { !it.isCurrentLocation }.toCollection(ArrayList())
                        )

                        weatherCurrentLocation = weatherCities
                            .firstOrNull { it.isCurrentLocation }
                        if (weatherCurrentLocation == null) {
                            binding.nameCurrentLocation.visibility = View.GONE
                            binding.titleCurrentLocation.text = this@WeatherActivity.getString(R.string.location_definition)
                        } else {
                            binding.nameCurrentLocation.visibility = View.VISIBLE
                            binding.titleCurrentLocation.text =
                                (this@WeatherActivity.getString(R.string.weather_current_location))
                            binding.nameCurrentLocation.text = weatherCurrentLocation!!.nameCity
                        }
                    }

                    resource.getEvent()?.let { event ->
                        Toast.makeText(
                            this@WeatherActivity,
                            this@WeatherActivity.getString(event), Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun locationServiceCollector() {
        lifecycleScope.apply {
            launch {
                locationService.getResource().collect { resource ->
                    resource.getData()?.let { nameCity ->
                        if (weatherCurrentLocation != null) {
                            weatherViewModel.updateWeatherCurrentLocation(nameCity)
                        } else {
                            weatherViewModel.createWeatherCurrentLocation(nameCity)
                        }
                        
                        binding.nameCurrentLocation.isClickable = true
                        binding.titleCurrentLocation.isClickable = true
                        isLocationInfoUpdated = true
                    }

                    resource.getEvent()?.let { event ->
                        Toast.makeText(
                            this@WeatherActivity,
                            this@WeatherActivity.getString(event), Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun createNewCity(@Suppress("UNUSED_PARAMETER") view: View) {
        val addingNewCity = binding.awAddingNewCity
        val nameCity = addingNewCity.text.toString()

        if (CheckStatusNetwork.isNetworkAvailable()) {
            if (nameCity.trim().isNotEmpty()) {
                addingNewCity.text.clear()
                addingNewCity.isCursorVisible = false

                weatherViewModel.createWeatherData(nameCity)
            } else {
                Toast.makeText(
                    this,
                    this.getString(R.string.city_name_not_empty),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                this,
                this.getString(R.string.no_internet_access),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun openWeatherDetailed(view: View) {
        val bindings: WRecWeatherCurrentBinding = WRecWeatherCurrentBinding.bind(view)

        startActivity(
            DetailedWeatherActivity.createIntent(this, bindings.wRecCityName.text as String, false)
        )
    }

    fun openWeatherCurrentLocation(@Suppress("UNUSED_PARAMETER") view: View) {
        when {
            isLocationInfoUpdated -> {
                startActivity(
                    DetailedWeatherActivity.createIntent(this,
                        binding.nameCurrentLocation.text as String, true)
                )
            }
            CheckStatusNetwork.isNetworkAvailable() -> {
                binding.nameCurrentLocation.isClickable = false
                binding.titleCurrentLocation.isClickable = false
                locationService.startLocationService(this)
            }
            else -> {
                Toast.makeText(
                    this,
                    R.string.no_internet_access,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
   }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PackageManager.PERMISSION_GRANTED -> {
                if (!(grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    Toast.makeText(
                        this,
                        R.string.permission_denied,
                        Toast.LENGTH_LONG
                    ).show()
                } else locationService.startLocationService(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                locationService.startLocationService(this)
            }

            Activity.RESULT_CANCELED -> {
                Toast.makeText(
                    this,
                    R.string.gps_disabled,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isLocationInfoUpdated = false
    }
}