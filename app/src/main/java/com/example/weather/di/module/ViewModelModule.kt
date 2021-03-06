package com.example.weather.di.module

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.weather.data.repository.Repository
import com.example.weather.di.qualifier.ActivityContext
import com.example.weather.di.scope.ActivityScope
import com.example.weather.viewmodel.DetailedWeatherViewModel
import com.example.weather.viewmodel.PrecipitationMapViewModel
import com.example.weather.viewmodel.ViewModelFactory
import com.example.weather.viewmodel.WeatherViewModel
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {

    @Provides
    @ActivityScope
    fun viewModelFactory(repository: Repository): ViewModelFactory {
        return ViewModelFactory(repository)
    }

    @Provides
    @ActivityScope
    fun weatherViewModel(@ActivityContext context: Context,
            viewModelFactory: ViewModelFactory): WeatherViewModel {
        return ViewModelProvider(context as ViewModelStoreOwner,
            viewModelFactory)[WeatherViewModel::class.java]
    }

    @Provides
    @ActivityScope
    fun detailedWeatherViewModel(@ActivityContext context: Context,
          viewModelFactory: ViewModelFactory): DetailedWeatherViewModel {
        return  ViewModelProvider(context as ViewModelStoreOwner,
            viewModelFactory)[DetailedWeatherViewModel::class.java]
    }

    @Provides
    @ActivityScope
    fun precipitationMapViewModel(@ActivityContext context: Context,
                                 viewModelFactory: ViewModelFactory): PrecipitationMapViewModel {
        return  ViewModelProvider(context as ViewModelStoreOwner,
            viewModelFactory)[PrecipitationMapViewModel::class.java]
    }
}