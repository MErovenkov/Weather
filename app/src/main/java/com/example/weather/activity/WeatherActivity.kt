package com.example.weather.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
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
import com.example.weather.location.LocationService
import com.example.weather.utils.extensions.*
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
    private val alphaNotUpdatedData = 0.5F
    private val alphaUpdatedData = 1F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent(this).inject(this)

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        viewModelCollector()
        locationServiceCollector()
        checkNetworkCollector()

        binding.currentLocation.alpha = alphaNotUpdatedData

        swipeRefreshLayout = binding.awSwipeFresh
        swipeRefreshLayout.setOnRefreshListener {
            if (CheckStatusNetwork.isNetworkAvailable()) {
                weatherViewModel.updateAllCitiesWeather()
                swipeRefreshLayout.isRefreshing = false
            } else {
                showNoInternetAccess()
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
        lifecycleScope.launch {
            weatherViewModel.getResource().collect { resource ->
                resource.getData()?.let { weatherCities ->
                    adapterRecyclerView.update(weatherCities.filter { !it.isCurrentLocation }
                        .toCollection(ArrayList()))

                    weatherCurrentLocation = weatherCities
                        .firstOrNull { it.isCurrentLocation }

                    if (weatherCurrentLocation == null ||
                        !this@WeatherActivity.hasLocationPermission()) {
                        binding.currentLocation.visibility = View.GONE
                        binding.titleCurrentLocation.text =
                            this@WeatherActivity.getString(R.string.location_definition)
                    } else {
                        binding.currentLocation.visibility = View.VISIBLE
                        binding.currentLocation.text = (weatherCurrentLocation!!.nameCity
                                + "\n" + weatherCurrentLocation!!.weatherCurrent.temperature)

                        binding.titleCurrentLocation.text =
                            (this@WeatherActivity.getString(R.string.weather_current_location))
                    }

                    resource.getEvent()?.let { event ->
                        if (weatherCities.filter { !it.isCurrentLocation }
                                .toCollection(ArrayList()).isNotEmpty()) {
                           this@WeatherActivity.showToast(event)
                        }
                    }
                }
            }
        }
    }

    private fun locationServiceCollector() {
        lifecycleScope.launch {
            locationService.getResource().collect { resource ->
                resource.getData()?.let { nameCity ->

                    if (weatherCurrentLocation != null) {
                        weatherViewModel.updateWeatherCurrentLocation(nameCity)
                    } else {
                        weatherViewModel.createWeatherCurrentLocation(nameCity)
                    }

                    binding.currentLocation.alpha = alphaUpdatedData
                }

                resource.getEvent()?.let { event ->
                    this@WeatherActivity.showToast(event)
                }

                binding.titleCurrentLocation.isClickable = true
            }
        }
    }

    private fun checkNetworkCollector() {
        lifecycleScope.launch {
            CheckStatusNetwork.getNetworkAvailable().collect {
                if (it) {
                    locationService.startLocationService(this@WeatherActivity)
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
                showToast(R.string.city_name_not_empty)
            }
        } else showNoInternetAccess()
    }

    fun openWeatherDetailed(view: View) {
        val bindings: WRecWeatherCurrentBinding = WRecWeatherCurrentBinding.bind(view)

        startActivity(
            DetailedWeatherActivity.createIntent(this,
                bindings.wRecCityName.text as String, false)
        )
    }

    fun openWeatherCurrentLocation(@Suppress("UNUSED_PARAMETER") view: View) {
        if (binding.titleCurrentLocation.text == this.getString(R.string.location_definition)) {
            if (CheckStatusNetwork.isNetworkAvailable()) {
                binding.titleCurrentLocation.isClickable = false

                locationService.startLocationService(this)
            } else showNoInternetAccess()
        } else {
            startActivity(
                weatherCurrentLocation?.let {
                    DetailedWeatherActivity.createIntent(
                        this,
                        it.nameCity, true
                    )
                }
            )
        }
   }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PackageManager.PERMISSION_GRANTED -> {
                if (!(grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showToast(R.string.permission_denied)
                } else locationService.startLocationService(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                showToast(R.string.loading_information)
            }

            Activity.RESULT_CANCELED -> {
                showToast(R.string.gps_disabled)
            }
        }
    }
}