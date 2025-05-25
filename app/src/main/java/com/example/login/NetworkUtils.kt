package com.example.login

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.net.ssl.HostnameVerifier

/**
 * ATENÇÃO: Esta função cria um OkHttpClient que IGNORA TODAS AS VERIFICAÇÕES SSL.
 * ISSO É EXTREMAMENTE INSEGURO E SÓ DEVE SER USADO EM AMBIENTES DE TESTE CONTROLADOS
 * COM SERVIDORES LOCAIS QUE USAM CERTIFICADOS AUTOASSINADOS.
 * NUNCA USE ESTE CÓDIGO EM PRODUÇÃO.
 */
fun getUnsafeOkHttpClient(): OkHttpClient {
    try {
        // Cria um trust manager que não valida cadeias de certificados
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }
        )

        // Instala o trust manager "confia-em-todos"
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        // Cria uma SSL socket factory com o nosso trust manager
        val sslSocketFactory = sslContext.socketFactory

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Ou .NONE para produção
        }

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier(HostnameVerifier { _, _ -> true }) // Desabilita a verificação do hostname
            .addInterceptor(logging) // Adiciona logging
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}