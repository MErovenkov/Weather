package com.merovenkov.weather.di.module

import android.content.Context
import com.merovenkov.weather.R
import com.merovenkov.weather.data.repository.api.OkHttpClientFactory
import com.merovenkov.weather.di.qualifier.ApplicationContext
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
class OkHttpClientModule {

    @Provides
    @Singleton
    fun customOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClientFactory.create(
                context.getString(R.string.protocol_tls),
                context.resources.openRawResource(R.raw.certificate_api_openweathermap),
                context.resources.openRawResource(R.raw.certificate_tile_openweathermap)
        )
    }
}