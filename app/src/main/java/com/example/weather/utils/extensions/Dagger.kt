package com.example.weather.utils.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.work.Worker
import com.example.weather.MyApplication
import com.example.weather.di.component.ActivityComponent
import com.example.weather.di.component.ApplicationComponent
import com.example.weather.di.component.FragmentComponent
import com.example.weather.ui.MainActivity

fun Worker.getApplicationComponent(): ApplicationComponent {
    return (this.applicationContext as MyApplication).appComponent
}

fun Fragment.getFragmentComponent(): FragmentComponent {
    return (this.requireContext() as MainActivity).activityComponent.fragmentComponent().create(this)
}

fun AppCompatActivity.getActivityComponent(): ActivityComponent {
    return (this.applicationContext as MyApplication).appComponent.activityComponent().create(this)
}