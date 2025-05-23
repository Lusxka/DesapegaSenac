package com.example.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ProdutosActivity : AppCompatActivity() {

    private lateinit var recyclerViewProdutos: RecyclerView
    private lateinit var produtoAdapter: ProdutoAdapter
    private lateinit var apiService: ApiService
    private lateinit var btnIrParaAdmin: Button
    private lateinit var logoutButton: Button
    private lateinit var searchEditText: EditText
    private lateinit var preferencesManager: PreferencesManager
    private var allProdutos: List<Produto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_produtos)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_produtos)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.products_title)

        preferencesManager = PreferencesManager(this)

        setupViews()
        setupSearch()
        setupRetrofit()
        getProdutos()
    }

    private fun setupViews() {
        btnIrParaAdmin = findViewById(R.id.btnIrParaAdmin)
        logoutButton = findViewById(R.id.logoutButton) // deve existir no XML
        searchEditText = findViewById(R.id.searchEditText)
        recyclerViewProdutos = findViewById(R.id.recyclerViewProdutos)
        recyclerViewProdutos.layoutManager = LinearLayoutManager(this)
        produtoAdapter = ProdutoAdapter(emptyList())
        recyclerViewProdutos.adapter = produtoAdapter

        btnIrParaAdmin.setOnClickListener {
            startActivity(Intent(this, AdminProdutosActivity::class.java))
        }

        logoutButton.setOnClickListener {
            preferencesManager.clearUser()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterProdutos(s?.toString() ?: "")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun filterProdutos(query: String) {
        if (query.isEmpty()) {
            produtoAdapter.updateProdutos(allProdutos)
            return
        }

        val filteredList = allProdutos.filter {
            it.produtoNome.contains(query, ignoreCase = true) ||
                    it.produtoDescricao.contains(query, ignoreCase = true)
        }
        produtoAdapter.updateProdutos(filteredList)
    }

    private fun setupRetrofit() {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = getUnsafeOkHttpClient().newBuilder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.56.1/meu_projeto_api/listagem/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    private fun getProdutos() {
        apiService.getProdutos().enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    allProdutos = response.body() ?: emptyList()
                    produtoAdapter.updateProdutos(allProdutos)
                } else {
                    Toast.makeText(this@ProdutosActivity, getString(R.string.error_loading_products), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Toast.makeText(this@ProdutosActivity, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
                Log.e("ProdutosActivity", "Network error: ${t.message}")
            }
        })
    }
}
