package com.merovenkov.weather

import android.app.Application
import androidx.work.*
import com.merovenkov.weather.di.component.ApplicationComponent
import com.merovenkov.weather.di.component.DaggerApplicationComponent
import com.merovenkov.weather.utils.CheckStatusNetwork
import com.merovenkov.weather.worker.NotificationWorker
import com.merovenkov.weather.worker.UpdateWorker
import java.util.concurrent.TimeUnit

class MyApplication: Application(){

     val appComponent: ApplicationComponent by lazy {
        initializeComponent()
    }

    private fun initializeComponent(): ApplicationComponent {
        return com.merovenkov.weather.di.component.DaggerApplicationComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        initWorkers()
        CheckStatusNetwork.registerNetworkCallback(applicationContext)
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