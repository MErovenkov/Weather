package com.example.weather.ui.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.R
import com.example.weather.databinding.FragmentWeatherBinding
import com.example.weather.location.LocationService
import com.example.weather.data.model.WeatherCity
import com.example.weather.ui.navigation.IWeatherNavigation
import com.example.weather.ui.recycler.GenericAdapter
import com.example.weather.ui.recycler.SwipeToDeleteCallback
import com.example.weather.utils.CheckStatusNetwork
import com.example.weather.utils.extensions.*
import com.example.weather.utils.resource.event.EventStatus
import com.example.weather.viewmodel.WeatherViewModel
import com.example.weather.worker.NotificationWorker
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class WeatherFragment: Fragment() {

    companion object {
        private const val ALPHA_NOT_UPDATED_DATA = 0.5F
        private const val ALPHA_UPDATED_DATA = 1F
    }

    @Inject
    lateinit var weatherViewModel: WeatherViewModel
    @Inject
    lateinit var locationService: LocationService
    @Inject
    lateinit var weatherNavigation: IWeatherNavigation

    private lateinit var binding: FragmentWeatherBinding
    private lateinit var adapterRecyclerView: GenericAdapter<WeatherCity>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var weatherCurrentLocation: WeatherCity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(layoutInflater)
        getFragmentComponent(requireContext()).inject(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initSwipeRefreshLayout()

        recyclerDataCollector()
        locationDataCollector()
        locationServiceCollector()

        binding.addingNewCity.setOnClickListener { createNewCity() }
        binding.titleCurrentLocation.setOnClickListener { openWeatherCurrentLocation() }
        binding.currentLocation.apply {
            setOnClickListener { openWeatherCurrentLocation() }
            alpha = ALPHA_NOT_UPDATED_DATA
        }
    }

    override fun onStart() {
        super.onStart()

        if (isLocationEnable()) {
            if (CheckStatusNetwork.isNetworkAvailable()) {
                if (hasLocationPermission()) {
                    locationService.startLocationService(this)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        locationService.stopLocationUpdates()
    }

    private fun initRecyclerView() {
        adapterRecyclerView = object : GenericAdapter<WeatherCity>(){
            override fun <T> itemDismiss(data: T) {
                weatherViewModel.deleteWeatherCity(data as WeatherCity)
                requireContext().cancelNotification(data.id)
            }
            override fun onClickItem(holder: RecyclerView.ViewHolder, position: Int) {
                holder.itemView.setOnClickListener {
                    weatherNavigation.openDetails(getItem<WeatherCity>(position).nameCity,
                        isCurrentLocation = false, hasAnimationOpening = true)
                }
            }
        }
        val recyclerView = binding.recyclerView.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterRecyclerView
        }
        val touchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapterRecyclerView))
        touchHelper.attachToRecyclerView(recyclerView)
    }

    private fun initSwipeRefreshLayout() {
        swipeRefreshLayout = binding.awSwipeFresh
        swipeRefreshLayout.setOnRefreshListener {
            if (CheckStatusNetwork.isNetworkAvailable()) {
                weatherViewModel.updateWeatherCities()
            } else {
                showNoInternetAccess()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun recyclerDataCollector() {
        viewLifecycleOwner.lifecycleScope.launch {
            weatherViewModel.getResourceRecycler().collect { resource ->
                resource.getData()?.let { weatherCities ->
                    adapterRecyclerView.update(weatherCities)
                }

                resource.getEvent()?.let { event ->
                    val eventStatus: Int? = event.getStatusIfNotHandled()

                    if (eventStatus != EventStatus.IS_NOT_REFRESHING) {
                        eventStatus?.let { this@WeatherFragment.showToast(it)}
                    }

                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    private fun locationDataCollector() {
        viewLifecycleOwner.lifecycleScope.launch {
            weatherViewModel.getResourceLocation().collect { resource ->
                resource.getData().let { weatherLocation ->
                    weatherCurrentLocation = weatherLocation

                    if (weatherCurrentLocation == null) {
                        showLocationDefinition()
                    } else if (!this@WeatherFragment.hasLocationPermission()) {
                        showLocationDefinition()
                        weatherViewModel.deleteWeatherCity(weatherCurrentLocation!!)
                        requireContext().cancelNotification(NotificationWorker.CURRENT_LOCATION_ID)
                    } else {
                        binding.currentLocation.visibility = View.VISIBLE
                        binding.currentLocation.text = (weatherCurrentLocation!!.nameCity
                                + "\n" + weatherCurrentLocation!!.weatherCurrent.temperature)

                        binding.titleCurrentLocation.text =
                            (this@WeatherFragment.getString(R.string.weather_current_location))
                    }
                }

                resource.getEvent()?.let { event ->
                    when (val eventStatus: Int? = event.getStatusIfNotHandled()) {
                        EventStatus.LOCATION_INFO_FAILURE -> {
                            showLocationDefinition()
                            this@WeatherFragment.showToast(EventStatus.LOCATION_INFO_FAILURE)
                        }

                        EventStatus.CURRENT_LOCATION_RECEIVED -> {
                            binding.currentLocation.alpha = ALPHA_UPDATED_DATA
                        }

                        else -> eventStatus?.let { this@WeatherFragment.showToast(it) }
                    }
                }
            }
        }
    }

    private fun showLocationDefinition() {
        binding.currentLocation.visibility = View.GONE
        binding.titleCurrentLocation.text =
            this@WeatherFragment.getString(R.string.location_definition)
    }

    private fun locationServiceCollector() {
        viewLifecycleOwner.lifecycleScope.launch {
            locationService.getResource().collect { resource ->
                resource.getData()?.let { locationData ->
                    if (locationData.name.isNullOrEmpty()) {
                        weatherViewModel
                            .createWeatherCurrentLocation(locationData.lat, locationData.lon)
                    } else weatherViewModel.createWeatherCurrentLocation(locationData.name)
                }

                resource.getEvent()?.let { event ->
                    val eventStatus: Int? = event.getStatusIfNotHandled()

                    eventStatus?.let { this@WeatherFragment.showToast(it) }
                }
            }
        }
    }

    private fun createNewCity() {
        val addingNewCity = binding.addingNewCity
        val nameCity = addingNewCity.text.toString()

        if (CheckStatusNetwork.isNetworkAvailable()) {
            if (nameCity.isNotBlank()) {
                addingNewCity.text.clear()
                addingNewCity.isCursorVisible = false

                weatherViewModel.createWeatherData(nameCity)
            } else addingNewCity.text.clear()
        } else {
            showNoInternetAccess()
        }
    }

    private fun openWeatherCurrentLocation() {
        if (binding.titleCurrentLocation.text == this.getString(R.string.location_definition)
            || binding.currentLocation.alpha == ALPHA_NOT_UPDATED_DATA) {
            if (CheckStatusNetwork.isNetworkAvailable()) {
                locationService.startLocationService(this)
            } else showNoInternetAccess()
        } else {
            weatherNavigation.openDetails(weatherCurrentLocation!!.nameCity,
                isCurrentLocation = true, hasAnimationOpening = true)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (!(grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
           Toast.makeText(
               requireContext(),
               this.getString(R.string.permission_denied),
               Toast.LENGTH_SHORT
           ).show()
        } else {
            locationService.startLocationService(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                Toast.makeText(
                    requireContext(),
                    this.getString(R.string.loading_information),
                    Toast.LENGTH_SHORT
                ).show()

                locationService.startLocationService(this)
            }

            Activity.RESULT_CANCELED -> {
                Toast.makeText(
                    requireContext(),
                    this.getString(R.string.gps_disabled),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}