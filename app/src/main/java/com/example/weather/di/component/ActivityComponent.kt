package com.example.weather.di.component

import android.content.Context
import com.example.weather.di.module.ViewModelModule
import com.example.weather.di.qualifier.ActivityContext
import com.example.weather.di.scope.ActivityScope
import com.example.weather.ui.fragment.DetailedWeatherFragment
import com.example.weather.ui.fragment.WeatherFragment
import dagger.BindsInstance
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ViewModelModule::class])
interface ActivityComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@ActivityContext @BindsInstance context: Context): ActivityComponent
    }

    fun inject(weatherFragment: WeatherFragment)
    fun inject(detailedWeatherFragment: DetailedWeatherFragment)
}