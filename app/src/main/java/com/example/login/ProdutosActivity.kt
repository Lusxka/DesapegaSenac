package com.example.login

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProdutosActivity : AppCompatActivity() {

    private lateinit var recyclerViewProdutos: RecyclerView
    private lateinit var produtoAdapter: ProdutoAdapter
    private lateinit var apiService: ApiService // Declaração da ApiService
    lateinit var nomeProduto: TextView
    lateinit var descProduto: TextView
    lateinit var precoProduto: TextView
    lateinit var descontoProduto: TextView
    lateinit var ativoProduto: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_produtos)

        // Referência ao TextView e definição do texto
        val textViewWelcome = findViewById<TextView>(R.id.textViewWelcome)
        textViewWelcome.text = "Bem-vindo à tela de Produtos!"

        // Configuração do RecyclerView
        recyclerViewProdutos = findViewById(R.id.recyclerViewProdutos)
        recyclerViewProdutos.layoutManager = LinearLayoutManager(this)
        produtoAdapter = ProdutoAdapter(emptyList()) // Inicializa com uma lista vazia
        recyclerViewProdutos.adapter = produtoAdapter

        // Configuração do Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.135.111.48/meu_projeto_api/") // Certifique-se de que o IP está correto e acessível
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java) // Inicializa a ApiService

        // Chamada para obter os produtos
        getProdutos()
    }

    private fun getProdutos() {
        val call = apiService.getProdutos()
        call.enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    val produtos = response.body() ?: emptyList()
                    produtoAdapter.updateProdutos(produtos) // Atualiza o adapter com os dados recebidos
                } else {
                    Toast.makeText(this@ProdutosActivity, "Erro ao obter produtos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Toast.makeText(this@ProdutosActivity, "Erro de rede: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    init {
        // Inicializando os componentes
        nomeProduto = findViewById(R.id.txtNomeProduto)
        descProduto = findViewById(R.id.txtDescProduto)
        precoProduto = findViewById(R.id.txtPrecoProduto)
        descontoProduto = findViewById(R.id.txtDescontoProduto)
        ativoProduto = findViewById(R.id.txtAtivoProduto)
    }
}
