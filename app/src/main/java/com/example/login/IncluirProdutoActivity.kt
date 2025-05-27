package com.example.login

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class IncluirProdutoActivity : AppCompatActivity() {

    private lateinit var toolbarIncluirProduto: Toolbar
    private lateinit var nomeEditText: TextInputEditText
    private lateinit var descricaoEditText: TextInputEditText
    private lateinit var precoEditText: TextInputEditText
    private lateinit var imagemEditText: TextInputEditText
    private lateinit var salvarButton: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incluir_produto)

        toolbarIncluirProduto = findViewById(R.id.toolbar_incluir_produto)
        setSupportActionBar(toolbarIncluirProduto)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        // O título é definido no XML

        nomeEditText = findViewById(R.id.nomeEditText)
        descricaoEditText = findViewById(R.id.descricaoEditText)
        precoEditText = findViewById(R.id.precoEditText)
        imagemEditText = findViewById(R.id.imagemEditText)
        salvarButton = findViewById(R.id.salvarButton)

        // Use o OkHttpClient centralizado e inseguro de NetworkUtils.kt
        val unsafeOkHttpClient = getUnsafeOkHttpClient()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.15.128/meu_projeto_api/listagem/") // Use a sua URL base correta
            .addConverterFactory(GsonConverterFactory.create())
            .client(unsafeOkHttpClient)
            .build()

        apiService = retrofit.create(ApiService::class.java)

        salvarButton.setOnClickListener {
            validarEIncluirProduto()
        }
    }

    private fun validarEIncluirProduto() {
        val nome = nomeEditText.text.toString().trim()
        val descricao = descricaoEditText.text.toString().trim()
        val preco = precoEditText.text.toString().trim()
        val imagem = imagemEditText.text.toString().trim()

        if (nome.isNotEmpty() && descricao.isNotEmpty() && preco.isNotEmpty() && imagem.isNotEmpty()) {
            incluirNovoProduto(nome, descricao, preco, imagem)
        } else {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
        }
    }

    private fun incluirNovoProduto(nome: String, descricao: String, preco: String, imagem: String) {
        apiService.incluirProduto(nome, descricao, preco, imagem).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@IncluirProdutoActivity, getString(R.string.product_added_successfully), Toast.LENGTH_SHORT).show()
                    finish() // Voltar para a tela de listagem
                } else {
                    val errorMsg = try {
                        response.errorBody()?.string() ?: getString(R.string.error_adding_product)
                    } catch (e: Exception) {
                        getString(R.string.error_adding_product)
                    }
                    Toast.makeText(this@IncluirProdutoActivity, "${getString(R.string.error_adding_product)}: ${response.code()} - $errorMsg", Toast.LENGTH_LONG).show()
                    Log.e("IncluirProdutoActivity", "Erro ao incluir: ${response.code()} - $errorMsg")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                val errorMessage = getString(R.string.error_network_adding_product, t.message ?: "Erro desconhecido")
                Toast.makeText(this@IncluirProdutoActivity, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("IncluirProdutoActivity", "Falha na rede ao incluir: ${t.message}", t)
            }
        })
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
}