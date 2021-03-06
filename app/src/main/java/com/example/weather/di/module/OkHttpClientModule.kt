package com.example.weather.di.module

import android.content.Context
import com.example.weather.R
import com.example.weather.data.repository.api.OkHttpClientFactory
import com.example.weather.di.qualifier.ApplicationContext
import com.example.weather.utils.HostSelectionInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
class OkHttpClientModule {

    companion object {
        private const val BASE_URL = "https://api.openweathermap.org"
    }

    @Provides
    @Singleton
    fun customOkHttpClient(@ApplicationContext context: Context,
                           hostSelectionInterceptor: HostSelectionInterceptor): OkHttpClient {
        return OkHttpClientFactory.create(
                context.getString(R.string.protocol_tls),
                context.resources.openRawResource(R.raw.certificate_api_openweathermap),
                context.resources.openRawResource(R.raw.certificate_tile_openweathermap),
                hostSelectionInterceptor
        )
    }

    @Provides
    @Singleton
    fun HostSelectionInterceptor(): HostSelectionInterceptor {
        return HostSelectionInterceptor(BASE_URL)
    }
}