package com.example.weather.worker

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker.Result.retry
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import com.example.weather.data.repository.Repository
import com.example.weather.utils.extensions.getApplicationComponent
import io.reactivex.rxjava3.core.Single
import androidx.work.rxjava3.RxWorker
import javax.inject.Inject

class UpdateWorker(context: Context, params: WorkerParameters): RxWorker(context, params) {

    companion object {
        const val NAME_WORKER = "updateWeatherData"
    }

    private val tag = this.javaClass.simpleName

    @Inject
    lateinit var repository: Repository

    init {
        getApplicationComponent().inject(this)
    }

    override fun createWork(): Single<Result> {
        return repository.updateWeatherCities()
            .map {
                Log.i(tag, "Update all cities weather is complete")
                success()
            }.onErrorReturn { e ->
                Log.w(tag, e.stackTraceToString())
                retry()
            }
    }
}