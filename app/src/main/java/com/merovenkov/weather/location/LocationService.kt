package com.merovenkov.weather.location

import android.Manifest
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.merovenkov.weather.utils.resource.LocationData
import com.merovenkov.weather.utils.resource.Resource
import com.merovenkov.weather.utils.resource.event.EventStatus
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
    private val tag = this.javaClass.simpleName

    private lateinit var geocoder: Geocoder
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

    private var resource: MutableStateFlow<Resource<LocationData>>
            = MutableStateFlow(Resource(null))
    fun getResource(): StateFlow<Resource<LocationData>> = resource.asStateFlow()

    init {
        createLocationCallback()
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Log.i(tag,"LocationResult received")
                convertLocationToAddress(locationResult.lastLocation)
            }
        }
        Log.i(tag, "Created locationCallback")
    }

    private fun convertLocationToAddress(location: Location) {
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

        if(addresses!!.isNotEmpty()) {
            val address = addresses[0]
            val cityName = with(address) {
                (0..maxAddressLineIndex).map { locality }
            }

            resource.value = Resource(LocationData(location.latitude, location.longitude,
                cityName[0]))

            Log.i(tag, "Locality received")
        } else resource.value = Resource(EventStatus.LOCATION_INFO_FAILURE)
    }

    fun startLocationService(fragment: Fragment) {
        if (ActivityCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

            fragment.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION), PackageManager.PERMISSION_GRANTED)
            Log.i(tag, "Request permission ACCESS_FINE_LOCATION")
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

                        geocoder = Geocoder(fragment.requireContext())
                        Log.i(tag, "RequestLocationUpdates")
                    }
                }
                .addOnFailureListener { e ->
                    if (e is ResolvableApiException) {
                        try {
                            fragment.startIntentSenderForResult(e.resolution.intentSender,
                                PackageManager.PERMISSION_GRANTED,
                                null, 0, 0, 0, null)
                            Log.i(tag, "Request gps to use")
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

            Log.i(tag,"Location Callback stopped")
        }
    }
}