package com.example.weather.di.module

import android.content.Context
import com.example.weather.di.qualifier.ApplicationContext
import com.example.weather.location.LocationService
import com.google.android.gms.location.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocationServiceModule {

    @Provides
    @Singleton
    fun locationService(
        settingsClient: SettingsClient,
        locationRequest: LocationRequest,
        locationSettingsRequest: LocationSettingsRequest,
    ): LocationService {
        return LocationService(settingsClient, locationRequest, locationSettingsRequest)
    }

    @Provides
    @Singleton
    fun settingClient(@ApplicationContext context: Context): SettingsClient {
        return LocationServices.getSettingsClient(context)
    }

    @Provides
    @Singleton
    fun locationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            interval = 100L
        }
    }

    @Provides
    @Singleton
    fun locationSettingsRequest(locationRequest: LocationRequest): LocationSettingsRequest {
        return LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
    }
}