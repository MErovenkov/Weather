package com.example.weather.di.module

import android.content.Context
import com.example.weather.di.qualifier.ActivityContext
import com.example.weather.di.scope.ActivityScope
import com.example.weather.ui.theme.IThemeManager
import com.example.weather.ui.theme.ThemeManager
import dagger.Module
import dagger.Provides

@Module
class ThemeManagerModule {

    @Provides
    @ActivityScope
    fun getThemeManager(@ActivityContext context: Context): IThemeManager {
        return ThemeManager(context)
    }
}