package com.example.weather.di.module

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.weather.R
import com.example.weather.di.qualifier.ActivityContext
import com.example.weather.di.scope.ActivityScope
import com.example.weather.ui.navigation.IWeatherNavigation
import com.example.weather.ui.navigation.Navigation
import dagger.Module
import dagger.Provides

@Module
class NavigationModule {

    @Provides
    @ActivityScope
    fun navController(@ActivityContext context: Context): NavController {
        return (context as AppCompatActivity).findNavController(R.id.fragment_container)
    }

    @Provides
    @ActivityScope
    fun weatherNavigation(navController: NavController): IWeatherNavigation {
        return Navigation(navController)
    }
}