package com.example.weather.utils.api

import com.example.weather.utils.api.interfaces.IWeatherApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class NetworkService() {
    private val BASE_URL = "https://api.openweathermap.org"
    private val hostName = "api.openweathermap.org"
    private val protocol = "SSL"
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private var retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpFactory.getCustomOkHttpClient(protocol, hostName))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    fun getWeatherApi(): IWeatherApi {
        return retrofit.create(IWeatherApi::class.java)
    }
}