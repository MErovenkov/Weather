package com.example.weather.utils.extensions

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.MyApplication
import com.example.weather.di.component.ActivityComponent

/**
 * App extension
 */
fun AppCompatActivity.getActivityComponent(context: Context): ActivityComponent {
    return (application as MyApplication).appComponent.activityComponent().create(context)
}