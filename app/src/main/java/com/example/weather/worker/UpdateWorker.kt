package com.example.weather.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weather.MyApplication
import com.example.weather.repository.Repository
import java.lang.Exception
import javax.inject.Inject

class UpdateWorker(context: Context, params: WorkerParameters,
    ) : Worker(context, params) {

    @Inject
    lateinit var repository: Repository

    init {
        (applicationContext as MyApplication).appComponent.inject(this)
    }

    override fun doWork(): Result {
        return try {
            repository.updateAllCitiesWeather()
            Log.d("WM", "Update all cities weather is complete")
            Result.success()
        } catch (e: Exception) {
            Log.w(e.toString(), e.stackTraceToString())
            Result.retry()
        }
    }
}