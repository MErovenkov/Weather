package com.example.weather.location

import android.Manifest
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.weather.utils.resource.Resource
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationService(
    private val settingsClient: SettingsClient,
    private val locationRequest: LocationRequest,
    private val locationSettingsRequest: LocationSettingsRequest,
) {
    private val TAG = this.javaClass.simpleName

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

    private var resource: MutableStateFlow<Resource<Location>>
            = MutableStateFlow(Resource(null))
    fun getResource(): StateFlow<Resource<Location>> = resource.asStateFlow()

    init {
        createLocationCallback()
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Log.i(TAG,"LocationResult received")
                resource.value = Resource(locationResult.lastLocation)
            }
        }
        Log.i(TAG, "Created locationCallback")
    }

    fun startLocationService(fragment: Fragment) {
        if (ActivityCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

            fragment.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION), PackageManager.PERMISSION_GRANTED)
            Log.i(TAG, "Request permission ACCESS_FINE_LOCATION")
        } else {
            settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnCompleteListener {
                    if (fusedLocationProviderClient == null) {
                        fusedLocationProviderClient =
                            LocationServices.getFusedLocationProviderClient(fragment.requireActivity())
                        fusedLocationProviderClient!!.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.myLooper()
                        )
                        Log.i(TAG, "RequestLocationUpdates")
                    }
                }
                .addOnFailureListener { e ->
                    if (e is ResolvableApiException) {
                        try {
                            fragment.startIntentSenderForResult(e.resolution.intentSender,
                                PackageManager.PERMISSION_GRANTED,
                                null, 0, 0, 0, null)
                            Log.i(TAG, "Request gps to use")
                        } catch (e: SendIntentException) {
                            Log.w(e.toString(), e.stackTraceToString())
                        }
                    }
                }
        }
    }

    fun stopLocationUpdates() {
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
            fusedLocationProviderClient = null

            Log.i(TAG,"Location Callback stopped")
        }
    }
}