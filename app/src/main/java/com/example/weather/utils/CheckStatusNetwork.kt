package com.example.weather.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest

object CheckStatusNetwork {
    private var isActive: Boolean = false
    private lateinit var mContext: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var networkRequest: NetworkRequest

    fun registerNetworkCallback(context: Context) {
        mContext = context.applicationContext
        connectivityManager =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        networkCallback = object :ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                isActive = true
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                isActive = false
            }
        }

        networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(
            networkRequest,
            networkCallback)
    }

    fun isNetworkAvailable(): Boolean {
        return isActive
    }
}