package com.example.weather.worker.provider

import androidx.work.*
import com.example.weather.worker.NotificationWorker
import com.example.weather.worker.UpdateWorker
import java.util.concurrent.TimeUnit

class CustomWorkManagerInitializer: DummyContentProvider() {
    override fun onCreate(): Boolean {
        WorkManager.initialize(context!!, Configuration.Builder().build())
        initWorkers()
        return true
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

        val instanceWorkManager = WorkManager.getInstance(context!!)

        instanceWorkManager.apply {
            enqueueUniquePeriodicWork(
                UpdateWorker.NAME_WORKER,
                ExistingPeriodicWorkPolicy.KEEP, updateWorkerRequest
            )

            enqueueUniquePeriodicWork(
                NotificationWorker.NAME_WORKER,
                ExistingPeriodicWorkPolicy.KEEP, notificationWorkRequest
            )
        }
    }
}