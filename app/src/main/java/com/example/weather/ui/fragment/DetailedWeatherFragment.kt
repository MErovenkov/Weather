package com.example.weather.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.databinding.FragmentDetailedWeatherBinding
import com.example.weather.data.model.WeatherFuture
import com.example.weather.utils.CheckStatusNetwork
import com.example.weather.utils.extensions.*
import com.example.weather.ui.recycler.GenericAdapter
import com.example.weather.viewmodel.DetailedWeatherViewModel
import com.yandex.metrica.YandexMetrica
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailedWeatherFragment: Fragment()  {

    companion object {
        private const val CITY_NAME_KEY = "cityName"
        private const val IS_CURRENT_LOCATION_KEY = "isCurrentLocation"

        fun getNewBundle(cityName: String, isCurrentLocation: Boolean): Bundle {
            return Bundle().apply {
                putString(CITY_NAME_KEY, cityName)
                putBoolean(IS_CURRENT_LOCATION_KEY, isCurrentLocation)
            }
        }
    }

    @Inject
    lateinit var detailedWeatherViewModel: DetailedWeatherViewModel

    private lateinit var binding: FragmentDetailedWeatherBinding
    private lateinit var adapterRecyclerView: GenericAdapter<WeatherFuture>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onAttach(context: Context) {
        super.onAttach(context)

        hideKeyboard()
        getActivityComponent(context).inject(this)

        YandexMetrica.reportEvent("Detailed weather is open")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detailedWeatherViewModel
            .initMutableStateFlow(requireArguments().getString(CITY_NAME_KEY).toString(),
                                  requireArguments().getBoolean(IS_CURRENT_LOCATION_KEY))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailedWeatherBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initSwipeRefreshLayout()

        viewModelCollector()
    }

    private fun initRecyclerView() {
        adapterRecyclerView = object : GenericAdapter<WeatherFuture>() {}
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context)
            adapter = adapterRecyclerView
        }
    }

    private fun initSwipeRefreshLayout() {
        swipeRefreshLayout = binding.adwSwipeFresh
        swipeRefreshLayout.setOnRefreshListener {
            if (CheckStatusNetwork.isNetworkAvailable()) {
                detailedWeatherViewModel.updateWeatherCity()
            } else {
                showNoInternetAccess()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun viewModelCollector() {
        viewLifecycleOwner.lifecycleScope.launch {
            detailedWeatherViewModel.getResource().collect { resource ->
                resource.getData()?.let { weatherCity ->
                    binding.apply {
                        cityName.text = weatherCity.nameCity
                        currentTemperature.text = weatherCity.weatherCurrent.temperature
                        iconCurrentWeather.setImageResource(
                            resources.getIdentifier(
                                "ic_current_w${weatherCity.weatherCurrent.nameIconWeather}",
                                "drawable", requireContext().packageName
                            )
                        )
                    }

                    adapterRecyclerView
                        .update(ArrayList(weatherCity.weatherFutureList))
                }

                resource.getEvent()?.let { event ->
                    val eventStatus: Int? = event.getStatusIfNotHandled()

                    eventStatus?.let { this@DetailedWeatherFragment.showToast(it) }
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }
}