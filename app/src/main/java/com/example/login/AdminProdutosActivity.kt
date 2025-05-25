package com.example.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class AdminProdutosActivity : AppCompatActivity(), ProdutoCallback {

    private lateinit var recyclerViewAdminProdutos: RecyclerView
    private lateinit var adminProdutoAdapter: AdminProdutoAdapter
    private lateinit var apiService: ApiService
    private lateinit var incluirProdutoButton: Button
    private lateinit var toolbarAdminProdutos: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_produtos)

        toolbarAdminProdutos = findViewById(R.id.toolbar_admin_produtos)
        setSupportActionBar(toolbarAdminProdutos)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Gerenciar Produtos"

        recyclerViewAdminProdutos = findViewById(R.id.recyclerViewAdminProdutos)
        recyclerViewAdminProdutos.layoutManager = LinearLayoutManager(this)

        incluirProdutoButton = findViewById(R.id.incluirProdutoButton)
        incluirProdutoButton.setOnClickListener {
            val intent = Intent(this, IncluirProdutoActivity::class.java)
            startActivity(intent)
        }

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val unsafeOkHttpClient = getUnsafeOkHttpClient().newBuilder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.1.113/meu_projeto_api/listagem/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(unsafeOkHttpClient)
            .build()

        apiService = retrofit.create(ApiService::class.java)

        getProdutos()
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getProdutos() {
        val call = apiService.getProdutos()
        call.enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    val produtos = response.body() ?: emptyList()
                    adminProdutoAdapter = AdminProdutoAdapter(produtos, apiService, this@AdminProdutosActivity)
                    recyclerViewAdminProdutos.adapter = adminProdutoAdapter
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@AdminProdutosActivity, "Erro ao obter produtos: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                    Log.e("AdminProdutosActivity", "Erro ao obter produtos: ${response.code()} - $errorBody")
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Toast.makeText(this@AdminProdutosActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("AdminProdutosActivity", "Erro de rede ao obter produtos: ${t.message}")
            }
        })
    }

    override fun onProdutoDeletado() {
        getProdutos()
    }

    override fun onResume() {
        super.onResume()
        getProdutos()
    }
}
