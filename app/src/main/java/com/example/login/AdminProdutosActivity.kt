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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// A interface ProdutoCallback é definida em AdminProdutoAdapter.kt
// (Mantenha apenas uma definição global para evitar conflitos)

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
        // O título é definido no XML via app:title="@string/admin_produtos_title"

        recyclerViewAdminProdutos = findViewById(R.id.recyclerViewAdminProdutos)
        recyclerViewAdminProdutos.layoutManager = LinearLayoutManager(this)

        incluirProdutoButton = findViewById(R.id.incluirProdutoButton)
        incluirProdutoButton.setOnClickListener {
            val intent = Intent(this, IncluirProdutoActivity::class.java)
            startActivity(intent)
        }

        // Use o OkHttpClient centralizado e inseguro de NetworkUtils.kt
        // Assumo que 'getUnsafeOkHttpClient()' está definido em um arquivo NetworkUtils.kt ou similar
        // (Você precisa garantir que NetworkUtils.kt e a função getUnsafeOkHttpClient() existam no seu projeto)
        val unsafeOkHttpClient = getUnsafeOkHttpClient() // A função já inclui o logging e timeouts

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.15.128/meu_projeto_api/listagem/") // Mantenha sua URL base
            .addConverterFactory(GsonConverterFactory.create())
            .client(unsafeOkHttpClient)
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // getProdutos() será chamado em onResume
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed() // Ou finish() se for o comportamento desejado
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getProdutos() {
        val call = apiService.getProdutos()
        call.enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    val produtos = response.body() ?: emptyList()
                    if (::adminProdutoAdapter.isInitialized) {
                        adminProdutoAdapter.updateData(produtos) // Chama o método updateData do adapter
                    } else {
                        adminProdutoAdapter = AdminProdutoAdapter(produtos, apiService, this@AdminProdutosActivity)
                        recyclerViewAdminProdutos.adapter = adminProdutoAdapter
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = getString(R.string.error_loading_products_with_code, "${response.code()} - $errorBody")
                    Toast.makeText(this@AdminProdutosActivity, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("AdminProdutosActivity", "Erro ao obter produtos: ${response.code()} - $errorBody")
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                val errorMessage = getString(R.string.error_network_loading_products, t.message ?: "Erro desconhecido")
                Toast.makeText(this@AdminProdutosActivity, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("AdminProdutosActivity", "Erro de rede ao obter produtos: ${t.message}", t)
            }
        })
    }

    // Implementação do ProdutoCallback
    override fun onProdutoDeletado() {
        Toast.makeText(this, getString(R.string.product_deleted_successfully), Toast.LENGTH_SHORT).show()
        getProdutos() // Atualiza a lista
    }

    override fun onProdutoNaoDeletado(mensagemErro: String) {
        val displayMessage = "${getString(R.string.error_deleting_product)}: $mensagemErro"
        Toast.makeText(this, displayMessage, Toast.LENGTH_LONG).show()
    }
    // Fim da Implementação do ProdutoCallback

    override fun onResume() {
        super.onResume()
        getProdutos() // Carrega/Atualiza os produtos sempre que a activity é resumida
    }
}