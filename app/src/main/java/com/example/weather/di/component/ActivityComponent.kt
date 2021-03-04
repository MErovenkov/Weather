package com.example.weather.di.component

import android.content.Context
import com.example.weather.di.module.NavigationActivityModule
import com.example.weather.di.module.SubFragmentModule
import com.example.weather.di.module.ThemeManagerModule
import com.example.weather.di.module.ViewModelModule
import com.example.weather.di.qualifier.ActivityContext
import com.example.weather.di.scope.ActivityScope
import com.example.weather.ui.MainActivity
import dagger.BindsInstance
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ViewModelModule::class, NavigationActivityModule::class,
                         SubFragmentModule::class, ThemeManagerModule::class])
interface ActivityComponent {
    interface Holder {
        val activityComponent: ActivityComponent
    }

    @Subcomponent.Factory
    interface Factory {
        fun create(@ActivityContext @BindsInstance context: Context): ActivityComponent
    }
    fun fragmentComponent(): FragmentComponent.Factory

    fun inject(myActivity: MainActivity)
}