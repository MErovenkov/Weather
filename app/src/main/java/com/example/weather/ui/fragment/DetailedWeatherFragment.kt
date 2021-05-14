package com.example.weather.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.R
import com.example.weather.data.model.WeatherCity
import com.example.weather.databinding.FragmentDetailedWeatherBinding
import com.example.weather.data.model.WeatherFuture
import com.example.weather.ui.navigation.IDetailedWeatherNavigation
import com.example.weather.utils.CheckStatusNetwork
import com.example.weather.utils.extensions.*
import com.example.weather.ui.recycler.GenericAdapter
import com.example.weather.utils.resource.event.EventStatus
import com.example.weather.viewmodel.DetailedWeatherViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class DetailedWeatherFragment: BaseFragment()  {

    companion object {
        private const val CITY_NAME_KEY = "cityName"
        private const val IS_CURRENT_LOCATION_KEY = "isCurrentLocation"
        private const val IS_DEEP_LINK_KEY = "isDeepLink"

        fun getNewBundle(cityName: String, isCurrentLocation: Boolean): Bundle {
            return Bundle().apply {
                putString(CITY_NAME_KEY, cityName)
                putBoolean(IS_CURRENT_LOCATION_KEY, isCurrentLocation)
                putBoolean(IS_DEEP_LINK_KEY, false)
            }
        }

        fun getNewBundleByDeepLinkData(cityName: String): Bundle {
            return Bundle().apply {
                putString(CITY_NAME_KEY, cityName)
                putBoolean(IS_DEEP_LINK_KEY, true)
            }
        }
    }

    @Inject
    lateinit var detailedWeatherViewModel: DetailedWeatherViewModel
    @Inject
    lateinit var detailedWeatherNavigation: IDetailedWeatherNavigation

    private lateinit var binding: FragmentDetailedWeatherBinding
    private lateinit var adapterRecyclerView: GenericAdapter<WeatherFuture>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var weatherCity: WeatherCity

    override fun onAttach(context: Context) {
        super.onAttach(context)

        hideKeyboard()
        getFragmentComponent().inject(this)
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

        subscribeDetailedWeather()

        if (requireArguments().getBoolean(IS_DEEP_LINK_KEY)) {
            detailedWeatherViewModel
                .initResourceByDeepLinkData(
                    requireArguments().getString(CITY_NAME_KEY).toString())
        } else {
            detailedWeatherViewModel
                .initResource(
                    requireArguments().getString(CITY_NAME_KEY).toString(),
                    requireArguments().getBoolean(IS_CURRENT_LOCATION_KEY))
        }

        if (requireArguments().getBoolean(IS_DEEP_LINK_KEY)) {
            visibilityElementsWithInfo(View.GONE)
            swipeRefreshLayout.isRefreshing = true
        }

        binding.iconPrecipitationMap.setOnClickListener {
            if (CheckStatusNetwork.isNetworkAvailable()) {
                try {
                    detailedWeatherNavigation.openPrecipitationMap(
                        weatherCity.nameCity, weatherCity.lat, weatherCity.lon
                    )
                } catch (e: UninitializedPropertyAccessException) {
                    showToast(R.string.refresh_your_weather_data)
                }
            } else {
                showNoInternetAccess()
            }
        }
        binding.linearLayout.updateAllPaddingByWindowInserts()
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

    private fun subscribeDetailedWeather() {
        compositeDisposable.add(detailedWeatherViewModel.getResourceDetailedWeather()
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe { resource ->
            resource.getData()?.let { weatherCity ->
                this@DetailedWeatherFragment.weatherCity = weatherCity

                binding.apply {
                    cityName.text = weatherCity.nameCity
                    currentTemperature.text = weatherCity.weatherCurrent.temperature
                    iconCurrentWeather.setImageResource(
                        resources.getIdentifier(
                            "ic_current_w${weatherCity.weatherCurrent.nameIconWeather}",
                            "drawable", requireContext().packageName)
                    )
                }
                adapterRecyclerView.update(ArrayList(weatherCity.weatherFutureList))
            }

            resource.getEvent()?.let { event ->
                val eventStatus: Int? = event.getStatusIfNotHandled()

                when {
                    isDeepLinkException(eventStatus) -> {
                        detailedWeatherNavigation.popBackStack()
                        eventStatus?.let { this@DetailedWeatherFragment.showToast(it) }
                    }

                    eventStatus == EventStatus.CITY_WEATHER_DATA_RECEIVED -> {
                        visibilityElementsWithInfo(View.VISIBLE)
                    }

                    else -> eventStatus?.let { this@DetailedWeatherFragment.showToast(it) }
                }
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun isDeepLinkException(eventStatus: Int?): Boolean {
        return (requireArguments().getBoolean(IS_DEEP_LINK_KEY)
                && (eventStatus == EventStatus.CITY_NOT_FOUND
                || eventStatus == EventStatus.LOST_INTERNET_ACCESS
                || eventStatus == EventStatus.REQUEST_LIMIT_EXCEEDED))
    }

    private fun visibilityElementsWithInfo(visibility: Int) {
        binding.cityName.visibility = visibility
        binding.currentTemperature.visibility = visibility
        binding.iconCurrentWeather.visibility = visibility
        binding.recyclerView.visibility = visibility
    }

    override fun onDestroyView() {
        super.onDestroyView()
        detailedWeatherViewModel.compositeDisposable.clear()
    }
}