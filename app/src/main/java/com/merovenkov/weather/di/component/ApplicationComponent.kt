package com.merovenkov.weather.di.component

import android.content.Context
import com.merovenkov.weather.di.module.*
import com.merovenkov.weather.di.qualifier.ApplicationContext
import com.merovenkov.weather.worker.NotificationWorker
import com.merovenkov.weather.worker.UpdateWorker
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataBaseHelperModule::class, OkHttpClientModule::class,
                      NetworkServiceModule::class, WeatherDataModule::class,
                      SubActivityModule::class, LocationServiceModule::class])
interface ApplicationComponent {
    @Component.Factory
    interface Factory {
        fun create(@ApplicationContext @BindsInstance context: Context): ApplicationComponent
    }
    fun activityComponent(): ActivityComponent.Factory

    fun inject(updateWorker: UpdateWorker)
    fun inject(notificationWorker: NotificationWorker)
}