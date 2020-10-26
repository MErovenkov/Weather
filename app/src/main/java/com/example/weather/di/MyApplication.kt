package com.example.weather.di

import android.app.Application
import com.example.weather.di.component.ApplicationComponent
import com.example.weather.di.component.DaggerApplicationComponent
import com.example.weather.utils.CheckStatusNetwork
import com.example.weather.view.toast.ShowToast

open class MyApplication: Application() {
    val appComponent: ApplicationComponent by lazy {
        initializeComponent()
    }

    open fun initializeComponent(): ApplicationComponent {
        return DaggerApplicationComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        CheckStatusNetwork.registerNetworkCallback(applicationContext)
        ShowToast.setContext(applicationContext)
    }
}