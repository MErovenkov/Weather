package com.example.weather.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.di.MyApplication
import com.example.weather.di.component.ActivityComponent

fun AppCompatActivity.getActivityComponent(context: Context): ActivityComponent {
    return (application as MyApplication).appComponent.activityComponent().create(context)
}