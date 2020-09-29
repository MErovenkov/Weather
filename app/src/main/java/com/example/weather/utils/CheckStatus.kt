package com.example.weather.utils

import android.content.Context
import android.net.ConnectivityManager

object CheckStatus {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetwork

        return activeNetworkInfo != null
    }
}