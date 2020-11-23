package com.example.weather

import android.app.Application
import androidx.work.*
import com.example.weather.di.component.ApplicationComponent
import com.example.weather.di.component.DaggerApplicationComponent
import com.example.weather.utils.CheckStatusNetwork
import com.example.weather.worker.UpdateWorker
import java.util.concurrent.TimeUnit

class MyApplication: Application(){

    companion object {
        private const val WORKER_NAME = "updateWeatherData"
    }

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
            4, TimeUnit.HOURS
        )
            .addTag(WORKER_NAME)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            WORKER_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            myWorkRequest
        )
    }
}
