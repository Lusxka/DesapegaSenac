package com.example.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom
import java.security.cert.X509Certificate

fun getUnsafeOkHttpClient(): OkHttpClient {
    return try {
        // Criar um TrustManager que não valida cadeias de certificado
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                @Throws(java.security.cert.CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
                }

                @Throws(java.security.cert.CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )

        // Instalar o TrustManager que aceita todos os certificados
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        // Criar um SocketFactory com essas configurações
        val sslSocketFactory = sslContext.socketFactory

        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        builder.hostnameVerifier(HostnameVerifier { hostname, session -> true })

        builder.build()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}

class ProdutosActivity : AppCompatActivity() {

    private lateinit var recyclerViewProdutos: RecyclerView
    private lateinit var produtoAdapter: ProdutoAdapter
    private lateinit var apiService: ApiService
    private lateinit var btnIrParaAdmin: Button // Adicione esta linha

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_produtos)

        btnIrParaAdmin = findViewById(R.id.btnIrParaAdmin) // Inicialize o botão
        btnIrParaAdmin.setOnClickListener {
            val intent = Intent(this, AdminProdutosActivity::class.java)
            startActivity(intent)
        }

        recyclerViewProdutos = findViewById(R.id.recyclerViewProdutos)
        recyclerViewProdutos.layoutManager = LinearLayoutManager(this)
        produtoAdapter = ProdutoAdapter(emptyList())
        recyclerViewProdutos.adapter = produtoAdapter

        // Inicializar o Logging Interceptor
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val unsafeOkHttpClient = getUnsafeOkHttpClient().newBuilder()
            .addInterceptor(logging) // Adicione o interceptor aqui
            .build()

        // Inicializar o Retrofit com o OkHttpClient inseguro e o interceptor
        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.15.128/meu_projeto_api/listagem/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(unsafeOkHttpClient)
            .build()

        apiService = retrofit.create(ApiService::class.java)

        getProdutos()
    }

    private fun getProdutos() {
        val call = apiService.getProdutos()
        call.enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    val produtos = response.body() ?: emptyList()
                    produtoAdapter.updateProdutos(produtos)
                } else {
                    Toast.makeText(this@ProdutosActivity, "Erro ao obter produtos: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Toast.makeText(this@ProdutosActivity, "Erro de rede: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}