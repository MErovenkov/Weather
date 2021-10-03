package com.example.weather.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.example.weather.R
import com.example.weather.ui.fragment.DetailedWeatherFragment
import com.example.weather.ui.fragment.PrecipitationMapFragment

class Navigation(private var navController: NavController): INavigation {

    override fun openDetails(nameCity: String, isCurrentLocation: Boolean,
                             hasAnimationOpening: Boolean) {
        navController.popBackStack(R.id.weatherFragment, false)
        navController.navigate(
            R.id.detailedWeatherFragment,
            DetailedWeatherFragment.getNewBundle(nameCity, isCurrentLocation),
            getCustomAnim(hasAnimationOpening)
        )
    }

    override fun openDetailsByDeepLinkData(nameCity: String) {
        navController.popBackStack(R.id.weatherFragment, false)
        navController.navigate(
            R.id.detailedWeatherFragment,
            DetailedWeatherFragment.getNewBundleByDeepLinkData(nameCity),
            getCustomAnim(false)
        )
    }

    override fun openPrecipitationMap(cityName: String, lat: String, lon: String) {
        navController.navigate(
            R.id.precipitationMapFragment,
            PrecipitationMapFragment.getNewBundle(cityName, lat, lon),
            getCustomAnim(true)
        )
    }

    private fun getCustomAnim(hasAnimationOpening: Boolean): NavOptions {
        return navOptions {
            anim {
                if (hasAnimationOpening) {
                    enter = R.anim.slide_in_down
                    exit = R.anim.slide_out_up
                }
                popEnter = R.anim.slide_in_up
                popExit = R.anim.slide_out_down
            }
        }
    }

    override fun popBackStack() {
        navController.popBackStack(R.id.weatherFragment, true)
        navController.navigate(R.id.weatherFragment)
    }
}