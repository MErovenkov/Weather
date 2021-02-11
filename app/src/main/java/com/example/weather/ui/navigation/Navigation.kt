package com.example.weather.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.example.weather.R
import com.example.weather.ui.fragment.DetailedWeatherFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class Navigation(private var navController: NavController): IWeatherNavigation {

    override fun openDetails(nameCity: String, isCurrentLocation: Boolean,
                             hasAnimationOpening: Boolean) {
        navController.popBackStack(R.id.weatherFragment, false)
        navController.navigate(
            R.id.detailedWeatherFragment,
            DetailedWeatherFragment.getNewBundle(nameCity, isCurrentLocation),
            getCustomAnim(hasAnimationOpening)
        )

        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "Detailed weather")
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "DetailedWeatherFragment")
        }
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