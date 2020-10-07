package com.example.weather.utils.api

import android.annotation.SuppressLint
import android.util.Log
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

object OkHttpFactory {
    fun getCustomOkHttpClient(protocol: String, hostName: String): OkHttpClient {
        try {
            val trustAllCerts: Array<TrustManager> = arrayOf(
                object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkClientTrusted(chain: Array<X509Certificate>,
                        authType: String) {}

                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkServerTrusted(chain: Array<X509Certificate>,
                        authType: String) {}

                    override fun getAcceptedIssuers(): Array<X509Certificate> { return arrayOf() }
                }
            )

            val sslContext = SSLContext.getInstance(protocol)
            sslContext.init(null, trustAllCerts, SecureRandom())

            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(
                sslSocketFactory,
                (trustAllCerts.first { trustManager -> trustManager is X509TrustManager }
                        as X509TrustManager)
            )
            builder.hostnameVerifier { _, session ->
                val hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier()
                hostnameVerifier.verify(hostName, session)
            }
            return builder.build()
        } catch (e: Exception) {
            Log.w(e.toString(), Thread.currentThread().stackTrace[2].toString())
            throw RuntimeException(e)
        }
    }
}