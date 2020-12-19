package com.example.weather.utils.extensions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.weather.MyApplication
import com.example.weather.di.component.ActivityComponent

/**
 * App extension
 */
fun AppCompatActivity.getActivityComponent(context: Context): ActivityComponent {
    return (application as MyApplication).appComponent.activityComponent().create(context)
}

fun AppCompatActivity.hasLocationPermission(): Boolean{
    return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}