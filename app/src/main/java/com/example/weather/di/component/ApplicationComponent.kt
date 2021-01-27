package com.example.weather.di.component

import android.content.Context
import com.example.weather.di.module.*
import com.example.weather.di.qualifier.ApplicationContext
import com.example.weather.worker.NotificationWorker
import com.example.weather.worker.UpdateWorker
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataBaseHelperModule::class, OkHttpClientModule::class,
                      NetworkServiceModule::class, WeatherDataModule::class,
                      SubComponentModule::class, LocationServiceModule::class])
interface ApplicationComponent {
    @Component.Factory
    interface Factory {
        fun create(@ApplicationContext @BindsInstance context: Context): ApplicationComponent
    }
    fun activityComponent(): ActivityComponent.Factory

    fun inject(updateWorker: UpdateWorker)
    fun inject(notificationWorker: NotificationWorker)
}