package com.example.weather.di.module

import android.content.Context
import android.util.Log
import com.example.weather.R
import com.example.weather.di.qualifier.ApplicationContext
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.inject.Singleton
import javax.net.ssl.*

@Module
class OkHttpClientModule {

    @Provides
    @Singleton
    fun customOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        try {
            val protocol = context.getString(R.string.protocol_tls)
            val certificateInputStream = context.resources.openRawResource(R.raw.certificate_openweathermap)

            val certificateFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
            val certificate: Certificate = certificateFactory.generateCertificate(certificateInputStream)

            val keyStoreType: String = KeyStore.getDefaultType()
            val keyStore: KeyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null)
            keyStore.setCertificateEntry("ca", certificate)

            val tManagerFactoryAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val trustManagerFactory = TrustManagerFactory.getInstance(tManagerFactoryAlgorithm)
            trustManagerFactory.init(keyStore)

            val sslContext = SSLContext.getInstance(protocol)
            sslContext.init(null, trustManagerFactory.trustManagers, null)

            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(
                sslSocketFactory,
                trustManagerFactory.trustManagers
                    .first { trustManager -> trustManager is X509TrustManager } as X509TrustManager)

            return builder.build()
        } catch (e: Exception) {
            Log.w(e.toString(), Thread.currentThread().stackTrace[2].toString())
            throw RuntimeException(e)
        }
    }
}