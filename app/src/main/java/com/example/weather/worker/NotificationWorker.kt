package com.example.weather.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ListenableWorker.Result.retry
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weather.R
import com.example.weather.data.model.WeatherCity
import com.example.weather.data.repository.Repository
import com.example.weather.ui.MainActivity
import com.example.weather.utils.extensions.cancelNotification
import com.example.weather.utils.extensions.getApplicationComponent
import io.reactivex.rxjava3.functions.Consumer
import javax.inject.Inject

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        const val NAME_WORKER = "notificationAlert"
        const val CURRENT_LOCATION_ID = 0
        private const val NOTIFICATION_CHANNEL = "weatherChannel"
    }

    private val tag = this.javaClass.simpleName

    @Inject
    lateinit var repository: Repository

    init {
        getApplicationComponent().inject(this)
    }

    override fun doWork(): Result {
        return try {
            sendNotification(getWeatherCitiesByAlerts())
            Log.i(tag, "Notification worker is complete")
            success()
        } catch (e: Exception) {
            Log.w(tag, e.stackTraceToString())
            retry()
        }
    }

    private fun sendNotification(weatherCities: ArrayList<WeatherCity>) {
        val uniqueWeatherCities: ArrayList<WeatherCity> = weatherCities
            .distinctBy { weatherCity -> weatherCity.nameCity } as ArrayList<WeatherCity>

        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL,
                applicationContext.getString(R.string.alert_about_bad_weather),
                NotificationManager.IMPORTANCE_DEFAULT)

            notificationManager.createNotificationChannel(channel)
        }

        for (weatherCity in uniqueWeatherCities) {
            val intent = MainActivity
                .createIntent(applicationContext, weatherCity.nameCity, weatherCity.isCurrentLocation)

            val pendingIntent = getActivity(applicationContext,
                                            getIdNotification(weatherCity), intent, 0)

            val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(weatherCity.nameCity)
                .setContentText(formatDescription(weatherCity.alertTomorrow))
                .setStyle(NotificationCompat
                    .BigTextStyle()
                    .bigText( formatDescription(weatherCity.alertTomorrow)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            notificationManager.notify(getIdNotification(weatherCity), notification.build())
        }
    }

    private fun getIdNotification(weatherCity: WeatherCity): Int {
        return if (weatherCity.isCurrentLocation) CURRENT_LOCATION_ID else weatherCity.id
    }

    private fun formatDescription(alertTomorrow: String): String {
        return "${applicationContext.getString(R.string.alert_for_tomorrow)}:\n${alertTomorrow}"
    }

    private fun getWeatherCitiesByAlerts(): ArrayList<WeatherCity>  {
        val dangerousCities: ArrayList<WeatherCity> = ArrayList()

        repository.updateWeatherCities().subscribe (Consumer {
            dangerousCities.addAll(it.getData()!!
                .filter { weatherCity ->  weatherCity.alertTomorrow.isNotBlank()})
        })

        repository.getCurrentLocationWeather()?.let { it ->
            repository.updateWeatherCity(it).subscribe (Consumer {
                if (it.getData()?.alertTomorrow!!.isNotBlank()) {
                    dangerousCities.add(it.getData()!!)
                } else {
                    applicationContext.cancelNotification(CURRENT_LOCATION_ID)
                }
            })
        }

        return dangerousCities
    }
}