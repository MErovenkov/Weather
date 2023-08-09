package com.merovenkov.weather.di.module

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.merovenkov.weather.di.qualifier.ActivityContext
import com.merovenkov.weather.di.scope.ActivityScope
import com.merovenkov.weather.ui.navigation.INavigation
import com.merovenkov.weather.ui.navigation.Navigation
import com.merovenkov.weather.R
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class NavigationActivityModule {

    companion object {
        private const val ACTIVITY_NAV_CONTROLLER = "ActivityNavController"
    }

    @Provides
    @ActivityScope
    @Named(ACTIVITY_NAV_CONTROLLER)
    fun navController(@ActivityContext context: Context): NavController {
        val navHostFragment = (context as AppCompatActivity)
            .supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        return navHostFragment.navController
    }

    @Provides
    @ActivityScope
    fun navigation(@Named(ACTIVITY_NAV_CONTROLLER) navController: NavController): INavigation {
        return Navigation(navController)
    }
}