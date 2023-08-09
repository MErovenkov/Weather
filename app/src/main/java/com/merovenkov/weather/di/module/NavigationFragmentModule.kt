package com.merovenkov.weather.di.module

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.merovenkov.weather.di.qualifier.FragmentQualifier
import com.merovenkov.weather.di.scope.FragmentScope
import com.merovenkov.weather.ui.navigation.IDetailedWeatherNavigation
import com.merovenkov.weather.ui.navigation.IWeatherNavigation
import com.merovenkov.weather.ui.navigation.Navigation
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