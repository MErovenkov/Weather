package com.example.weather.utils.extensions

import android.app.NotificationManager
import android.content.Context
import com.example.weather.utils.CheckStatusNetwork

fun CheckStatusNetwork.isNetworkAvailable(): Boolean {
    return getNetworkAvailable().value
}

fun Context.cancelNotification(idNotification: Int) {
    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(idNotification)
}