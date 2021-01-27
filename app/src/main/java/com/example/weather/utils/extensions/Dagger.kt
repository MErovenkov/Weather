package com.example.weather.utils.extensions

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.work.Worker
import com.example.weather.MyApplication
import com.example.weather.di.component.ActivityComponent
import com.example.weather.di.component.ApplicationComponent

fun Worker.getApplicationComponent(): ApplicationComponent {
    return (this.applicationContext as MyApplication).appComponent
}

fun Fragment.getActivityComponent(context: Context): ActivityComponent {
    return (context.applicationContext as MyApplication).appComponent.activityComponent().create(context)
}

fun AppCompatActivity.getActivityComponent(context: Context): ActivityComponent {
    return (context.applicationContext as MyApplication).appComponent.activityComponent().create(context)
}