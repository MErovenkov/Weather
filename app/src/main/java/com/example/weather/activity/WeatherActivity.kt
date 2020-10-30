package com.example.weather.activity

import android.os.Bundle
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
import javax.inject.Inject

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
                weatherViewModel.updateWeatherData()
                swipeRefreshLayout.isRefreshing = false
            } else {
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
            } else ShowToast.getToast(application.getString(R.string.city_name_not_empty))
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