package com.example.weather.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.weather.R
import com.example.weather.utils.Resource
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

    private lateinit var geocoder: Geocoder
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

    private var resource: MutableStateFlow<Resource<String>>
            = MutableStateFlow(Resource(null))
    fun getResource(): StateFlow<Resource<String>> = resource.asStateFlow()

    init {
        createLocationCallback()
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Log.i(TAG,"LocationResult received")
                convertLocationToAddress(locationResult.lastLocation)
            }
        }
        Log.i(TAG, "Created locationCallback")
    }

    private fun convertLocationToAddress(location: Location) {
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

        if(addresses.isNotEmpty()) {
            val address = addresses[0]
            val cityName = with(address) {
                (0..maxAddressLineIndex).map { locality }
            }

            resource.value = Resource(cityName.joinToString())
            Log.i(TAG, "Locality received")
        } else resource.value = Resource(R.string.location_information_updated_failure)
    }

    fun startLocationService(context: Context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION), PackageManager.PERMISSION_GRANTED)
            Log.i(TAG, "Request permission ACCESS_FINE_LOCATION")
        } else {
            settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnCompleteListener {
                    if (fusedLocationProviderClient == null) {
                        fusedLocationProviderClient =
                            LocationServices.getFusedLocationProviderClient(context)
                        fusedLocationProviderClient!!.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.myLooper()
                        )

                        geocoder = Geocoder(context)
                        Log.i(TAG, "RequestLocationUpdates")
                    }
                }
                .addOnFailureListener { e ->
                    if (e is ResolvableApiException) {
                        try {
                            e.startResolutionForResult(context as Activity?, PackageManager.PERMISSION_GRANTED)
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