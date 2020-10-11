package com.example.weather.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.core.net.ConnectivityManagerCompat
import com.example.weather.R
import com.example.weather.view.toast.ShowToast

object CheckStatus {
    @SuppressLint("ServiceCast")
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isActive: Boolean

        isActive = if (Build.VERSION.SDK_INT >= 23) {
            connectivityManager.activeNetwork != null
        } else {
            ConnectivityManagerCompat.isActiveNetworkMetered(connectivityManager)
        }

        if (!isActive) {
            ShowToast.getToast(
                context,
                context.resources.getString(R.string.no_internet_access))
        }
        return isActive
    }
}