package com.merovenkov.weather.di.component

import androidx.fragment.app.Fragment
import com.merovenkov.weather.di.module.NavigationFragmentModule
import com.merovenkov.weather.di.qualifier.FragmentQualifier
import com.merovenkov.weather.di.scope.FragmentScope
import com.merovenkov.weather.ui.fragment.DetailedWeatherFragment
import com.merovenkov.weather.ui.fragment.PrecipitationMapFragment
import com.merovenkov.weather.ui.fragment.WeatherFragment
import dagger.BindsInstance
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [NavigationFragmentModule::class])
interface FragmentComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@FragmentQualifier @BindsInstance fragment: Fragment): FragmentComponent
    }

    fun inject(weatherFragment: WeatherFragment)
    fun inject(detailedWeatherFragment: DetailedWeatherFragment)
    fun inject(precipitationMapFragment: PrecipitationMapFragment)
}