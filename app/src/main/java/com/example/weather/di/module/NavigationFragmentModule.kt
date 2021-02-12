package com.example.weather.di.module

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.weather.di.qualifier.FragmentQualifier
import com.example.weather.di.scope.FragmentScope
import com.example.weather.ui.navigation.IDetailedWeatherNavigation
import com.example.weather.ui.navigation.IWeatherNavigation
import com.example.weather.ui.navigation.Navigation
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class NavigationFragmentModule {

    companion object {
        private const val FRAGMENT_NAV_CONTROLLER = "FragmentNavController"
    }

    @Provides
    @FragmentScope
    @Named(FRAGMENT_NAV_CONTROLLER)
    fun navController(@FragmentQualifier fragment: Fragment): NavController {
        return fragment.findNavController()
    }

    @Provides
    @FragmentScope
    fun weatherNavigation(@Named(FRAGMENT_NAV_CONTROLLER) navController: NavController)
            : IWeatherNavigation {
        return Navigation(navController)
    }

    @Provides
    @FragmentScope
    fun detailedWeatherNavigation(@Named(FRAGMENT_NAV_CONTROLLER) navController: NavController)
            : IDetailedWeatherNavigation {
        return Navigation(navController)
    }
}