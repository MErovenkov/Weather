package com.merovenkov.weather.di.component

import android.content.Context
import com.merovenkov.weather.di.module.NavigationActivityModule
import com.merovenkov.weather.di.module.SubFragmentModule
import com.merovenkov.weather.di.module.ViewModelModule
import com.merovenkov.weather.di.qualifier.ActivityContext
import com.merovenkov.weather.di.scope.ActivityScope
import com.merovenkov.weather.ui.MainActivity
import dagger.BindsInstance
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ViewModelModule::class, NavigationActivityModule::class,
                         SubFragmentModule::class])
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