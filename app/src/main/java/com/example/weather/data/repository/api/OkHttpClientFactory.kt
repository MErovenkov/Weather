package com.example.weather.data.repository.api

import android.util.Log
import com.example.weather.utils.HostSelectionInterceptor
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
               certificateTileInputStream: InputStream, hostSelectionInterceptor: HostSelectionInterceptor
        ): OkHttpClient {

        try {
            val certificateFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
            val certificate: Certificate = certificateFactory.generateCertificate(certificateApiInputStream)

            val certificate1: Certificate = certificateFactory.generateCertificate(certificateTileInputStream)

            val keyStoreType: String = KeyStore.getDefaultType()
            val keyStore: KeyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null)
            keyStore.setCertificateEntry("ca", certificate)
            keyStore.setCertificateEntry("ct", certificate1)

            val tManagerFactoryAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val trustManagerFactory = TrustManagerFactory.getInstance(tManagerFactoryAlgorithm)
            trustManagerFactory.init(keyStore)

            val sslContext = SSLContext.getInstance(protocol)
            sslContext.init(null, trustManagerFactory.trustManagers, null)

            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder().addInterceptor(hostSelectionInterceptor)
            builder.sslSocketFactory(
                sslSocketFactory,
                trustManagerFactory.trustManagers
                    .first { trustManager -> trustManager is X509TrustManager } as X509TrustManager)

            return builder.build()
        } catch (e: Exception) {
            Log.w(e.toString(), e.stackTraceToString())
            throw RuntimeException(e)
        }
    }
}