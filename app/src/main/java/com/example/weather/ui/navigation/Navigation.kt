package com.example.weather.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.example.weather.R
import com.example.weather.ui.fragment.DetailedWeatherFragment

class Navigation(private var navController: NavController): IWeatherNavigation {

    override fun openDetails(nameCity: String, isCurrentLocation: Boolean,
                             hasAnimationOpening: Boolean) {
        navController.popBackStack(R.id.weatherFragment, false)
        navController.navigate(
            R.id.detailedWeatherFragment,
            DetailedWeatherFragment.getNewBundle(nameCity, isCurrentLocation),
            getCustomAnim(hasAnimationOpening)
        )
    }

    private fun getCustomAnim(hasAnimationOpening: Boolean): NavOptions {
        return navOptions {
            anim {
                if (hasAnimationOpening) {
                    enter = R.anim.slide_in_right
                    exit = R.anim.slide_out_left
                }
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }
    }
}