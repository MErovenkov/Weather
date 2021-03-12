package com.example.weather.utils

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response

class HostSelectionInterceptor(private var host: String): Interceptor {

    fun setHost(host: String) {
        this.host = host
    }

    override fun intercept(chain: Chain): Response {
        var request: Request = chain.request()
        val newUrl: HttpUrl = request.url().newBuilder()
            .host(host)
            .build()

        request = request.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(request)
    }
}