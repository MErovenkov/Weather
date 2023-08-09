package com.merovenkov.weather.di.module

import android.content.Context
import com.merovenkov.weather.di.qualifier.ApplicationContext
import com.merovenkov.weather.utils.MapperWeatherData
import com.merovenkov.weather.data.repository.api.WeatherApiRequester
import com.merovenkov.weather.data.repository.api.WeatherData
import com.merovenkov.weather.data.repository.api.interfaces.IWeatherApi
import com.merovenkov.weather.utils.ApiKeyChanger
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