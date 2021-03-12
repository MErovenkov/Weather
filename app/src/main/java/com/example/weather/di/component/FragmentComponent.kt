package com.example.weather.di.component

import androidx.fragment.app.Fragment
import com.example.weather.di.module.NavigationFragmentModule
import com.example.weather.di.qualifier.FragmentQualifier
import com.example.weather.di.scope.FragmentScope
import com.example.weather.ui.fragment.DetailedWeatherFragment
import com.example.weather.ui.fragment.PrecipitationMapFragment
import com.example.weather.ui.fragment.WeatherFragment
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