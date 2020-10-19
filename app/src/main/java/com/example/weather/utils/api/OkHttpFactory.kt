package com.example.weather.utils.api

import android.util.Log
import okhttp3.OkHttpClient
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.net.ssl.*

object OkHttpFactory {
    fun getCustomOkHttpClient(protocol: String, certificate: InputStream): OkHttpClient {
        try {
            val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
            val ca: Certificate = cf.generateCertificate(certificate)

            val keyStoreType: String = KeyStore.getDefaultType()
            val keyStore: KeyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null)
            keyStore.setCertificateEntry("ca", ca)

            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
            tmf.init(keyStore)

            val sslContext = SSLContext.getInstance(protocol)
            sslContext.init(null, tmf.trustManagers, null)

            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(
                sslSocketFactory,
                tmf.trustManagers.first { trustManager -> trustManager is X509TrustManager }
                        as X509TrustManager)

            return builder.build()
        } catch (e: Exception) {
            Log.w(e.toString(), Thread.currentThread().stackTrace[2].toString())
            throw RuntimeException(e)
        }
    }
}