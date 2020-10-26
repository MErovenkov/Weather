package com.example.weather.di.module

import android.app.Application
import android.content.Context
import com.example.weather.dao.OrmLiteHelper
import com.example.weather.api.WeatherData
import com.example.weather.di.qualifier.ApplicationContext
import com.example.weather.di.scope.ActivityScope
import com.example.weather.viewmodel.ViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {

    @Provides
    @ActivityScope
    fun viewModelFactory(@ApplicationContext applicationContext: Context,
                         dataBaseHelper: OrmLiteHelper,
                         weatherData: WeatherData): ViewModelFactory {
        return ViewModelFactory(applicationContext as Application, dataBaseHelper, weatherData)
    }
}