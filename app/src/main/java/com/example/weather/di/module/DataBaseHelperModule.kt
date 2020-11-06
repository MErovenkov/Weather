package com.example.weather.di.module

import android.content.Context
import com.example.weather.repository.Repository
import com.example.weather.repository.api.WeatherData
import com.example.weather.repository.dao.OrmLiteHelper
import com.example.weather.di.qualifier.ApplicationContext
import com.j256.ormlite.android.apptools.OpenHelperManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataBaseHelperModule {

    @Provides
    @Singleton
    fun repository(dataBaseHelper: OrmLiteHelper,
                   weatherData: WeatherData): Repository {
        return Repository(dataBaseHelper, weatherData)
    }

    @Provides
    @Singleton
    fun ormLiteHelper(@ApplicationContext context: Context): OrmLiteHelper {
        return OpenHelperManager.getHelper(context, OrmLiteHelper::class.java)
    }
}