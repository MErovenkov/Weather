package com.example.weather.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ListenableWorker.Result.retry
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import androidx.work.rxjava3.RxWorker
import com.example.weather.R
import com.example.weather.data.model.WeatherCity
import com.example.weather.data.repository.Repository
import com.example.weather.ui.MainActivity
import com.example.weather.utils.extensions.cancelNotification
import com.example.weather.utils.extensions.getApplicationComponent
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class NotificationWorker(context: Context, params: WorkerParameters): RxWorker(context, params) {

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

    override fun createWork(): Single<Result> {
        return repository.updateWeatherCities()
            .map { resource -> resource.getData() }
            .flattenAsObservable { weatherCityList -> weatherCityList }
            .mergeWith(
                repository.getCurrentLocationWeather()?.let { weatherCity ->
                    repository.updateWeatherCity(weatherCity)
                        .map { resource -> resource?.getData() }.toMaybe()
                } ?: Maybe.empty()
            )
            .filter { weatherCity -> checkingAlertTomorrow(weatherCity) }
            .toList()
            .map {
                sendNotification(it)
                Log.i(tag, "Notification worker is complete")
                success()
            }.onErrorReturn { e ->
                Log.w(tag, e.stackTraceToString())
                retry()
            }
    }

    private fun checkingAlertTomorrow(weatherCity: WeatherCity):Boolean {
        return if(weatherCity.alertTomorrow.isNotBlank()) {
            true
        } else {
            if (weatherCity.isCurrentLocation) {
                applicationContext.cancelNotification(CURRENT_LOCATION_ID)
            }
            false
        }
    }

    private fun sendNotification(weatherCities: List<WeatherCity>) {
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
}