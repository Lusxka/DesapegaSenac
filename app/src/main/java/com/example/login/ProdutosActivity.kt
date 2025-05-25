package com.example.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom

class ProdutosActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var recyclerViewProdutos: RecyclerView
    private lateinit var produtoAdapter: ProdutoAdapter
    private lateinit var apiService: ApiService
    private lateinit var searchEditText: EditText
    private lateinit var preferencesManager: PreferencesManager

    private var allProdutos: List<Produto> = emptyList()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_produtos)

        toolbar = findViewById(R.id.toolbar_produtos)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.products_title)

        preferencesManager = PreferencesManager(this)

        setupViews()
        setupSearch()
        setupRetrofit()
        getProdutos()
        setupNavigationDrawer()
    }

    private fun setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> showToast("Home clicado")
            R.id.nav_sobre -> showToast("Sobre clicado")
            R.id.nav_minha_conta -> showToast("Minha Conta clicado")
            R.id.nav_politica_privacidade -> showToast("Política e Privacidade clicado")
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupViews() {
        searchEditText = findViewById(R.id.searchEditText)
        recyclerViewProdutos = findViewById(R.id.recyclerViewProdutos)
        recyclerViewProdutos.layoutManager = LinearLayoutManager(this)

        produtoAdapter = ProdutoAdapter(emptyList()) { produto ->
            val bottomSheet = ProdutoDetailBottomSheet.newInstance(produto)
            bottomSheet.show(supportFragmentManager, ProdutoDetailBottomSheet.TAG)
        }
        recyclerViewProdutos.adapter = produtoAdapter
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
                    showToast(getString(R.string.error_loading_products))
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                showToast(getString(R.string.network_error))
                Log.e("ProdutosActivity", "Network error: ${t.message}")
            }
        })
    }

    private fun getUnsafeOkHttpClient(): okhttp3.OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        return okhttp3.OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
