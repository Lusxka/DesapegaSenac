package com.example.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem // Importação necessária
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle // Importação necessária
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat // Importação necessária
import androidx.drawerlayout.widget.DrawerLayout // Importação necessária
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView // Importação necessária
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ProdutosActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener { // Implementar Listener

    private lateinit var recyclerViewProdutos: RecyclerView
    private lateinit var produtoAdapter: ProdutoAdapter
    private lateinit var apiService: ApiService
    private lateinit var btnIrParaAdmin: Button
    // private lateinit var logoutButton: Button // Comentado pois não está no XML de activity_produtos.xml
    // Se você adicionou o botão ao XML, descomente.
    private lateinit var searchEditText: EditText
    private lateinit var preferencesManager: PreferencesManager
    private var allProdutos: List<Produto> = emptyList()

    // Variáveis para o Navigation Drawer
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var toolbar: Toolbar // Tornar toolbar uma variável de classe para acesso no setupNavDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_produtos)

        toolbar = findViewById(R.id.toolbar_produtos) // Inicializar aqui
        setSupportActionBar(toolbar)
        // supportActionBar?.setDisplayHomeAsUpEnabled(true) // O Toggle fará isso
        supportActionBar?.title = getString(R.string.products_title)

        preferencesManager = PreferencesManager(this)

        setupViews()
        setupSearch()
        setupRetrofit()
        getProdutos()
        setupNavigationDrawer() // Chamar a configuração do Navigation Drawer
    }

    private fun setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        // Configurar o ActionBarDrawerToggle
        // O toggle é o ícone "hamburger" que abre e fecha o drawer
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar, // Passar a toolbar aqui
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState() // Sincroniza o estado do toggle com o drawerLayout

        // Definir o listener para os cliques nos itens do menu
        navigationView.setNavigationItemSelectedListener(this)
    }

    // Lida com os cliques nos itens do Navigation Drawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                Toast.makeText(this, "Home clicado", Toast.LENGTH_SHORT).show()
                // Exemplo: startActivity(Intent(this, HomeActivity::class.java))
            }
            R.id.nav_sobre -> {
                Toast.makeText(this, "Sobre clicado", Toast.LENGTH_SHORT).show()
                // Exemplo: startActivity(Intent(this, SobreActivity::class.java))
            }
            R.id.nav_minha_conta -> {
                Toast.makeText(this, "Minha Conta clicado", Toast.LENGTH_SHORT).show()
                // Exemplo: startActivity(Intent(this, MinhaContaActivity::class.java))
            }
            R.id.nav_politica_privacidade -> {
                Toast.makeText(this, "Política e Privacidade clicado", Toast.LENGTH_SHORT).show()
                // Exemplo: startActivity(Intent(this, PoliticaPrivacidadeActivity::class.java))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START) // Fecha o drawer após o clique
        return true // Indica que o evento foi tratado
    }

    // Permite que o ActionBarDrawerToggle lide com o evento de clique no ícone do menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Se o drawer estiver aberto, o botão "voltar" do Android deve fechá-lo
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    private fun setupViews() {
        btnIrParaAdmin = findViewById(R.id.btnIrParaAdmin)
        // logoutButton = findViewById(R.id.logoutButton) // Se adicionou no XML, descomente
        searchEditText = findViewById(R.id.searchEditText)
        recyclerViewProdutos = findViewById(R.id.recyclerViewProdutos)
        recyclerViewProdutos.layoutManager = LinearLayoutManager(this)
        produtoAdapter = ProdutoAdapter(emptyList())
        recyclerViewProdutos.adapter = produtoAdapter

        btnIrParaAdmin.setOnClickListener {
            startActivity(Intent(this, AdminProdutosActivity::class.java))
        }

        // Se adicionou o botão de logout ao XML e descomentou a variável,
        // o código abaixo pode ser usado.
        /*
        logoutButton.setOnClickListener {
            preferencesManager.clearUser()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        */
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

        val client = getUnsafeOkHttpClient().newBuilder() // Supondo que getUnsafeOkHttpClient() exista
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