package com.example.weather

import android.app.Application
import com.example.weather.di.component.ApplicationComponent
import com.example.weather.di.component.DaggerApplicationComponent
import com.example.weather.utils.CheckStatusNetwork
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig

class MyApplication: Application(){

     val appComponent: ApplicationComponent by lazy {
        initializeComponent()
    }

    private fun initializeComponent(): ApplicationComponent {
        return DaggerApplicationComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        initAppMetrica()
        YandexMetrica.reportEvent("App started")
        CheckStatusNetwork.registerNetworkCallback(applicationContext)
    }

    private fun initAppMetrica() {
        val config = YandexMetricaConfig
            .newConfigBuilder(applicationContext.resources.getString(R.string.app_metrica_api_key)).build()

        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)
    }
}