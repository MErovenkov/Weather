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
import com.example.weather.utils.extensions.getActivityComponent
import com.example.weather.location.LocationService
import com.example.weather.utils.extensions.isNetworkAvailable
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent(this).inject(this)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        viewModelCollector()
        locationServiceCollector()
        checkNetworkCollector()

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
                    adapterRecyclerView.update(
                        weatherCities
                            .filter { !it.isCurrentLocation }.toCollection(ArrayList())
                    )

                    weatherCurrentLocation = weatherCities
                        .firstOrNull { it.isCurrentLocation }

                    if (weatherCurrentLocation == null) {
                        binding.currentLocation.visibility = View.GONE
                        binding.titleCurrentLocation.text = this@WeatherActivity.getString(R.string.location_definition)
                    } else {
                        binding.currentLocation.visibility = View.VISIBLE
                        binding.titleCurrentLocation.text =
                            (this@WeatherActivity.getString(R.string.weather_current_location))

                        binding.currentLocation.text = (weatherCurrentLocation!!.nameCity
                                + "\n" + weatherCurrentLocation!!.weatherCurrent.temperature)
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

    private fun locationServiceCollector() {
        lifecycleScope.launch {
            locationService.getResource().collect { resource ->
                resource.getData()?.let { nameCity ->

                    if (weatherCurrentLocation != null) {
                        weatherViewModel.updateWeatherCurrentLocation(nameCity)
                    } else {
                        weatherViewModel.createWeatherCurrentLocation(nameCity)
                    }

                    binding.currentLocation.isClickable = true
                    binding.titleCurrentLocation.isClickable = true
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

    private fun checkNetworkCollector() {
        lifecycleScope.launch {
            CheckStatusNetwork.getNetworkAvailable().collect {
                if (it) {
                    locationService.startLocationService(this@WeatherActivity)
                } else {
                    showNoInternetAccess()
                }
            }
        }
    }

    private fun showNoInternetAccess() {
        Toast.makeText(
            this,
            this.getString(R.string.no_internet_access),
            Toast.LENGTH_SHORT
        ).show()
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
        } else showNoInternetAccess()
    }

    fun openWeatherDetailed(view: View) {
        val bindings: WRecWeatherCurrentBinding = WRecWeatherCurrentBinding.bind(view)

        startActivity(
            DetailedWeatherActivity.createIntent(this, bindings.wRecCityName.text as String, false)
        )
    }

    fun openWeatherCurrentLocation(@Suppress("UNUSED_PARAMETER") view: View) {
        if (binding.titleCurrentLocation.text == this.getString(R.string.location_definition)) {
            if (CheckStatusNetwork.isNetworkAvailable()) {
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
                    Toast.makeText(
                        this,
                        R.string.permission_denied,
                        Toast.LENGTH_LONG
                    ).show()
                    binding.currentLocation.visibility = View.GONE
                    binding.titleCurrentLocation.text = this@WeatherActivity.getString(R.string.location_definition)
                } else locationService.startLocationService(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                Toast.makeText(
                    this,
                    R.string.loading_information,
                    Toast.LENGTH_LONG
                ).show()
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
}