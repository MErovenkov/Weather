package com.example.weather.utils.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.work.rxjava3.RxWorker
import com.example.weather.MyApplication
import com.example.weather.di.component.ActivityComponent
import com.example.weather.di.component.ApplicationComponent
import com.example.weather.di.component.FragmentComponent

fun RxWorker.getApplicationComponent(): ApplicationComponent {
    return (this.applicationContext as MyApplication).appComponent
}

fun Fragment.getFragmentComponent(): FragmentComponent {
    return (this.requireContext() as ActivityComponent.Holder)
        .activityComponent.fragmentComponent().create(this)
}

fun AppCompatActivity.getActivityComponent(): ActivityComponent {
    return (this.applicationContext as MyApplication).appComponent.activityComponent().create(this)
}