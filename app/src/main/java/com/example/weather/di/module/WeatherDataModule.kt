package com.example.weather.di.module

import android.content.Context
import com.example.weather.di.qualifier.ApplicationContext
import com.example.weather.utils.MapperWeatherData
import com.example.weather.data.repository.api.WeatherApiRequester
import com.example.weather.data.repository.api.WeatherData
import com.example.weather.data.repository.api.interfaces.IWeatherApi
import com.example.weather.utils.ApiKeyChanger
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
    fun weatherApiRequester(weatherApiServer: IWeatherApi,
                            apiKeyChanger: ApiKeyChanger): WeatherApiRequester {
        return WeatherApiRequester(weatherApiServer, apiKeyChanger)
    }

    @Provides
    @Singleton
    fun apiKeyChanger(@ApplicationContext context: Context): ApiKeyChanger {
        return ApiKeyChanger(context)
    }

    @Provides
    @Singleton
    fun mapperWeatherData(@ApplicationContext context: Context): MapperWeatherData {
        return MapperWeatherData(context)
    }
}