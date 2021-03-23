package com.example.weather.data.repository.api

import android.util.Log
import okhttp3.OkHttpClient
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object OkHttpClientFactory {
    fun create(protocol: String, certificateApiInputStream: InputStream,
               certificateTileInputStream: InputStream
        ): OkHttpClient {

        try {
            val certificateFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
            val certificateApi: Certificate = certificateApiInputStream
                .use { certificateFactory.generateCertificate(it) }

            val certificateTile: Certificate = certificateTileInputStream
                .use { certificateFactory.generateCertificate(it) }

            val keyStoreType: String = KeyStore.getDefaultType()
            val keyStore: KeyStore = KeyStore.getInstance(keyStoreType).apply {
                load(null)
                setCertificateEntry("ca", certificateApi)
                setCertificateEntry("ct", certificateTile)
            }

            val tManagerFactoryAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val trustManagerFactory = TrustManagerFactory
                .getInstance(tManagerFactoryAlgorithm).apply { init(keyStore) }

            val sslContext = SSLContext.getInstance(protocol).apply {
                init(null, trustManagerFactory.trustManagers, null)
            }

            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder().apply {
                sslSocketFactory(
                sslSocketFactory,
                trustManagerFactory.trustManagers
                    .first { trustManager -> trustManager is X509TrustManager } as X509TrustManager)
            }

            return builder.build()
        } catch (e: Exception) {
            Log.w(e.toString(), e.stackTraceToString())
            throw RuntimeException(e)
        }
    }
}