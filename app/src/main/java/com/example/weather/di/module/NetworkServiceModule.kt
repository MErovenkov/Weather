package com.example.weather.di.module

import com.example.weather.repository.api.interfaces.IWeatherApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetworkServiceModule {
    @Provides
    @Singleton
    fun weatherApi(retrofit: Retrofit): IWeatherApi {
        return retrofit.create(IWeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun retrofit(okHttpClient: OkHttpClient, moshiConverterFactory: MoshiConverterFactory)
            : Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org")
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun moshiConverterFactory(moshi: Moshi): MoshiConverterFactory {
        return MoshiConverterFactory.create(moshi)
    }

    @Provides
    @Singleton
    fun moshi(): Moshi {
        return Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }
}