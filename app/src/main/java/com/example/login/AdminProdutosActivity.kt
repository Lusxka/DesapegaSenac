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
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AdminProdutosActivity : AppCompatActivity() {

    private lateinit var recyclerViewAdminProdutos: RecyclerView
    private lateinit var adminProdutoAdapter: AdminProdutoAdapter
    private lateinit var apiService: ApiService
    private lateinit var incluirProdutoButton: Button
    private lateinit var toolbarAdminProdutos: Toolbar // Adicione a declaração da Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_produtos)

        toolbarAdminProdutos = findViewById(R.id.toolbar_admin_produtos) // Inicialize a Toolbar pelo ID
        setSupportActionBar(toolbarAdminProdutos)

        // Habilita o botão "Voltar" na Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Gerenciar Produtos" // Opcional: Define um título para a Toolbar

        recyclerViewAdminProdutos = findViewById(R.id.recyclerViewAdminProdutos)
        recyclerViewAdminProdutos.layoutManager = LinearLayoutManager(this)

        incluirProdutoButton = findViewById(R.id.incluirProdutoButton)
        incluirProdutoButton.setOnClickListener {
            val intent = Intent(this, IncluirProdutoActivity::class.java)
            startActivity(intent)
        }

        // Configuração do Retrofit
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val unsafeOkHttpClient = getUnsafeOkHttpClient().newBuilder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.15.128/meu_projeto_api/listagem/") // Substitua pela sua URL base correta
            .addConverterFactory(GsonConverterFactory.create())
            .client(unsafeOkHttpClient)
            .build()

        apiService = retrofit.create(ApiService::class.java)

        getProdutos()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed() // Simula o pressionamento do botão "Voltar" do sistema
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
                    adminProdutoAdapter = AdminProdutoAdapter(produtos, apiService)
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

    override fun onResume() {
        super.onResume()
        // Recarrega a lista quando a Activity volta ao foreground
        getProdutos()
    }
}