package com.example.weather.activity

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
import com.example.weather.utils.getActivityComponent
import com.example.weather.view.recycler.GenericAdapter
import com.example.weather.view.recycler.SwipeToDeleteCallback
import com.example.weather.viewmodel.WeatherViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class WeatherActivity: AppCompatActivity() {
    @Inject
    lateinit var weatherViewModel: WeatherViewModel

    private lateinit var binding: ActivityWeatherBinding
    private lateinit var adapterRecyclerView: GenericAdapter<WeatherCity>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent(this).inject(this)

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        lifecycleScope.launch {
            weatherViewModel.getResource().collect { resource ->
                resource.getData()?.let { weatherCities ->
                    adapterRecyclerView.update(weatherCities)
                }

                resource.getEvent()?.let { event ->
                    Toast.makeText(this@WeatherActivity,
                    this@WeatherActivity.getString(event), Toast.LENGTH_SHORT).show()
                }
            }
        }

        swipeRefreshLayout = binding.awSwipeFresh
        swipeRefreshLayout.setOnRefreshListener {
            if (CheckStatusNetwork.isNetworkAvailable()) {
                weatherViewModel.updateWeatherData()
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

                weatherViewModel.createWeatherData(nameCity)
            } else {
                Toast.makeText(this,
                    this.getString(R.string.city_name_not_empty),
                    Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this,
                this.getString(R.string.no_internet_access),
                Toast.LENGTH_SHORT).show()
        }
    }

    fun openWeatherDetailed(view: View) {
        val bindings: WRecWeatherCurrentBinding = WRecWeatherCurrentBinding.bind(view)

        startActivity(
            DetailedWeatherActivity.createIntent(this, bindings.wRecCityName.text as String)
        )
    }
}