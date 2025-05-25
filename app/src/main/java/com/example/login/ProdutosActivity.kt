package com.example.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
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
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class ProdutosActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var recyclerViewProdutos: RecyclerView
    private lateinit var produtoAdapter: ProdutoAdapter
    private lateinit var apiService: ApiService
    private lateinit var searchEditText: EditText
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var toolbar: Toolbar
    private lateinit var bannerImageView: ImageView // Adicionado o banner ImageView para inicialização

    private var allProdutos: List<Produto> = emptyList()

    // Constante para o nome do arquivo SharedPreferences
    private val PREF_NAME = "user_session"
    // Constante para a chave de verificação de login
    private val KEY_IS_LOGGED_IN = "is_logged_in"
    // Adicione esta linha para o ImageView de pesquisa, se ele estiver no layout
    private lateinit var searchIcon: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_produtos)

        // Configura toolbar
        toolbar = findViewById(R.id.toolbar_produtos)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.products_title)

        // Inicialize o bannerImageView
        bannerImageView = findViewById(R.id.bannerImageView)
        // Opcional: Se você quiser setar a imagem via código (já está no XML)
        // bannerImageView.setImageResource(R.drawable.banner_desapega_senac)

        setupViews()
        setupSearch()
        setupRetrofit()
        getProdutos()
        setupNavigationDrawer()

        // Adicionando a inicialização do searchIcon, se ele existir no seu layout
        searchIcon = findViewById(R.id.searchIcon)
        searchIcon.setOnClickListener {
            val query = searchEditText.text.toString()
            Toast.makeText(this, "Pesquisando por: $query", Toast.LENGTH_SHORT).show()
            filterProdutos(query) // Chame a função de filtro
        }
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
            R.id.nav_home -> {
                // Se já estiver na Home, apenas fecha o drawer ou recarrega, se necessário
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_admin -> startActivity(Intent(this, AdminProdutosActivity::class.java))
            R.id.nav_sobre -> startActivity(Intent(this, SobreActivity::class.java))
            R.id.nav_minha_conta -> startActivity(Intent(this, MinhaContaActivity::class.java))
            R.id.nav_politica_privacidade -> startActivity(Intent(this, PoliticaPrivacidadeActivity::class.java))
            R.id.nav_logout -> {
                performLogout() // Chama a função de logout
            }
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
        } else {
            val filteredList = allProdutos.filter {
                it.produtoNome.contains(query, ignoreCase = true) ||
                        it.produtoDescricao.contains(query, ignoreCase = true)
            }
            produtoAdapter.updateProdutos(filteredList)
        }
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
                    Toast.makeText(
                        this@ProdutosActivity,
                        getString(R.string.error_loading_products),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Toast.makeText(
                    this@ProdutosActivity,
                    getString(R.string.network_error),
                    Toast.LENGTH_SHORT
                ).show()
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
        val sslContext = SSLContext.getInstance("SSL").apply {
            init(null, trustAllCerts, SecureRandom())
        }
        return okhttp3.OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    // --- Funções de SharedPreferences e Logout ---

    /**
     * Marca o usuário como logado no SharedPreferences.
     * Deve ser chamada após um login bem-sucedido.
     */
    fun setLoggedIn(isLoggedIn: Boolean) {
        val sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            apply() // Use apply() para salvar assincronamente
        }
    }

    /**
     * Verifica se o usuário está logado consultando o SharedPreferences.
     */
    fun isLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(KEY_IS_LOGGED_IN, false) // Default é false (não logado)
    }

    /**
     * Realiza a operação de logout:
     * 1. Limpa o estado de login no SharedPreferences.
     * 2. Redireciona para a tela de login.
     * 3. Finaliza a atividade atual para prevenir o retorno.
     */
    private fun performLogout() {
        setLoggedIn(false) // Define o usuário como não logado

        // Redireciona para a tela de Login
        val intent = Intent(this, LoginActivity::class.java) // Assumindo LoginActivity como a tela de login
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Limpa a pilha de atividades
        startActivity(intent)

        finish() // Finaliza a atividade atual
        Toast.makeText(this, "Você saiu do aplicativo.", Toast.LENGTH_SHORT).show()
    }
}