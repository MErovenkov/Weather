package com.merovenkov.weather.utils.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.work.Worker
import com.merovenkov.weather.MyApplication
import com.merovenkov.weather.di.component.ActivityComponent
import com.merovenkov.weather.di.component.ApplicationComponent
import com.merovenkov.weather.di.component.FragmentComponent

fun Worker.getApplicationComponent(): ApplicationComponent {
    return (this.applicationContext as MyApplication).appComponent
}

fun Fragment.getFragmentComponent(): FragmentComponent {
    return (this.requireContext() as ActivityComponent.Holder)
        .activityComponent.fragmentComponent().create(this)
}

fun AppCompatActivity.getActivityComponent(): ActivityComponent {
    return (this.applicationContext as MyApplication).appComponent.activityComponent().create(this)
}