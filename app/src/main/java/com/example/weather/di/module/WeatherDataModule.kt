package com.example.weather.di.module

import android.content.Context
import com.example.weather.R
import com.example.weather.di.qualifier.ApplicationContext
import com.example.weather.utils.MapperWeatherData
import com.example.weather.repository.api.WeatherApiRequester
import com.example.weather.repository.api.WeatherData
import com.example.weather.repository.api.interfaces.IWeatherApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class WeatherDataModule {

    @Provides
    @Singleton
    fun weatherData(weatherApiRequester: WeatherApiRequester,
                    mapperWeatherData: MapperWeatherData): WeatherData {
        return WeatherData(weatherApiRequester, mapperWeatherData)
    }

    @Provides
    @Singleton
    fun weatherApiRequester(weatherApiServer: IWeatherApi, @ApplicationContext context: Context): WeatherApiRequester {
        return WeatherApiRequester(weatherApiServer,
            context.getString(R.string.open_weather_map_api_key))
    }

    @Provides
    @Singleton
    fun mapperWeatherData(@ApplicationContext context: Context): MapperWeatherData {
        return MapperWeatherData(context)
    }
}