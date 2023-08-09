package com.merovenkov.weather.di.module

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.merovenkov.weather.data.repository.Repository
import com.merovenkov.weather.di.qualifier.ActivityContext
import com.merovenkov.weather.di.scope.ActivityScope
import com.merovenkov.weather.viewmodel.DetailedWeatherViewModel
import com.merovenkov.weather.viewmodel.PrecipitationMapViewModel
import com.merovenkov.weather.viewmodel.ViewModelFactory
import com.merovenkov.weather.viewmodel.WeatherViewModel
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