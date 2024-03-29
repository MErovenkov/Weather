package com.merovenkov.weather.di.module

import android.content.Context
import com.merovenkov.weather.data.repository.Repository
import com.merovenkov.weather.data.repository.api.WeatherData
import com.merovenkov.weather.data.repository.dao.OrmLiteHelper
import com.merovenkov.weather.di.qualifier.ApplicationContext
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