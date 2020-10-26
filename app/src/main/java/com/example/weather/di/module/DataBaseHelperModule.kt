package com.example.weather.di.module

import android.content.Context
import com.example.weather.dao.OrmLiteHelper
import com.example.weather.di.qualifier.ApplicationContext
import com.j256.ormlite.android.apptools.OpenHelperManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataBaseHelperModule {

    @Provides
    @Singleton
    fun ormLiteHelper(@ApplicationContext context: Context): OrmLiteHelper {
        return OpenHelperManager.getHelper(context, OrmLiteHelper::class.java)
    }
}