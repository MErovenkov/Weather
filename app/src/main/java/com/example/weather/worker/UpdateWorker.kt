package com.example.weather.worker

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker.Result.retry
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weather.data.repository.Repository
import com.example.weather.utils.extensions.getApplicationComponent
import java.lang.Exception
import javax.inject.Inject

class UpdateWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        const val NAME_WORKER = "updateWeatherData"
    }

    private val tag = this.javaClass.simpleName

    @Inject
    lateinit var repository: Repository

    init {
        getApplicationComponent().inject(this)
    }

    override fun doWork(): Result {
        return try {
            repository.updateWeatherCities()
            Log.i(tag, "Update all cities weather is complete")
            success()
        } catch (e: Exception) {
            Log.w(tag, e.stackTraceToString())
            retry()
        }
    }
}