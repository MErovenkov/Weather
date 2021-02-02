package com.example.weather

import android.app.Application
import androidx.work.*
import com.example.weather.di.component.ApplicationComponent
import com.example.weather.di.component.DaggerApplicationComponent
import com.example.weather.utils.CheckStatusNetwork
import com.example.weather.worker.NotificationWorker
import com.example.weather.worker.UpdateWorker
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
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
        initWorkers()
        CheckStatusNetwork.registerNetworkCallback(applicationContext)

        Firebase.analytics.logEvent("App_started", null)
    }

    private fun initWorkers() {
        val constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val updateWorkerRequest =  PeriodicWorkRequest.Builder(
            UpdateWorker::class.java,
            4, TimeUnit.HOURS
        ).addTag(UpdateWorker.NAME_WORKER).setConstraints(constraints).build()

        val notificationWorkRequest =  PeriodicWorkRequest.Builder(
            NotificationWorker::class.java,
            12, TimeUnit.HOURS
        ).addTag(NotificationWorker.NAME_WORKER).setConstraints(constraints).build()

        val instanceWorkManager = WorkManager.getInstance(applicationContext)

        instanceWorkManager.apply {
            enqueueUniquePeriodicWork(UpdateWorker.NAME_WORKER,
                ExistingPeriodicWorkPolicy.KEEP, updateWorkerRequest)

            enqueueUniquePeriodicWork(NotificationWorker.NAME_WORKER,
                ExistingPeriodicWorkPolicy.KEEP, notificationWorkRequest)
        }
    }
}