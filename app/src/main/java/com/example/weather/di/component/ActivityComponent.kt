package com.example.weather.di.component

import com.example.weather.activity.DetailedWeatherActivity
import com.example.weather.activity.WeatherActivity
import com.example.weather.di.module.ViewModelModule
import com.example.weather.di.scope.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ViewModelModule::class])
interface ActivityComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ActivityComponent
    }

    fun inject(weatherActivity: WeatherActivity)
    fun inject(detailedWeatherActivity: DetailedWeatherActivity)
}