package com.example.weather.di

import android.app.Application
import androidx.work.*
import com.example.weather.di.component.ApplicationComponent
import com.example.weather.di.component.DaggerApplicationComponent
import com.example.weather.utils.CheckStatusNetwork
import com.example.weather.worker.UpdateWorker
import java.util.concurrent.TimeUnit

class MyApplication: Application(){

     val appComponent: ApplicationComponent by lazy {
        initializeComponent()
    }

    private fun initializeComponent(): ApplicationComponent {
        return DaggerApplicationComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        initWorker()

        CheckStatusNetwork.registerNetworkCallback(applicationContext)
    }

    private fun initWorker() {
        val constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val myWorkRequest =  PeriodicWorkRequest.Builder(
            UpdateWorker::class.java,
            4, TimeUnit.HOURS)
            .addTag("updateWeatherData")
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
        "updateWeatherData",
        ExistingPeriodicWorkPolicy.KEEP,
        myWorkRequest)
    }
}
