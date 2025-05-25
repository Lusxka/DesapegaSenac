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

class EditarProdutoActivity : AppCompatActivity() {

    private lateinit var toolbarEditarProduto: Toolbar
    private lateinit var idEditText: TextInputEditText
    private lateinit var nomeEditText: TextInputEditText
    private lateinit var descricaoEditText: TextInputEditText
    private lateinit var precoEditText: TextInputEditText
    private lateinit var imagemEditText: TextInputEditText
    private lateinit var salvarButton: Button
    private lateinit var apiService: ApiService
    private var produtoId: Int = -1

    companion object {
        const val EXTRA_PRODUTO_ID = "PRODUTO_ID"
        const val EXTRA_PRODUTO_NOME = "PRODUTO_NOME"
        const val EXTRA_PRODUTO_DESC = "PRODUTO_DESC"
        const val EXTRA_PRODUTO_PRECO = "PRODUTO_PRECO"
        const val EXTRA_PRODUTO_IMAGEM = "PRODUTO_IMAGEM"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_produto)

        toolbarEditarProduto = findViewById(R.id.toolbar_editar_produto)
        setSupportActionBar(toolbarEditarProduto)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        // O título é definido no XML

        idEditText = findViewById(R.id.idEditText)
        nomeEditText = findViewById(R.id.nomeEditText)
        descricaoEditText = findViewById(R.id.descricaoEditText)
        precoEditText = findViewById(R.id.precoEditText)
        imagemEditText = findViewById(R.id.imagemEditText)
        salvarButton = findViewById(R.id.salvarButton)

        // Use o OkHttpClient centralizado e inseguro de NetworkUtils.kt
        val unsafeOkHttpClient = getUnsafeOkHttpClient()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.56.1/meu_projeto_api/listagem/") // Substitua pela sua URL correta
            .addConverterFactory(GsonConverterFactory.create())
            .client(unsafeOkHttpClient)
            .build()

        apiService = retrofit.create(ApiService::class.java)

        produtoId = intent.getIntExtra(EXTRA_PRODUTO_ID, -1)
        if (produtoId == -1 && savedInstanceState != null) { // Restaura ID se activity foi recriada
            produtoId = savedInstanceState.getInt(EXTRA_PRODUTO_ID, -1)
        }

        if (savedInstanceState == null) { // Só preenche da intent se não for recriação
            idEditText.setText(if (produtoId != -1) produtoId.toString() else "")
            nomeEditText.setText(intent.getStringExtra(EXTRA_PRODUTO_NOME))
            descricaoEditText.setText(intent.getStringExtra(EXTRA_PRODUTO_DESC))
            precoEditText.setText(intent.getStringExtra(EXTRA_PRODUTO_PRECO))
            imagemEditText.setText(intent.getStringExtra(EXTRA_PRODUTO_IMAGEM))
        }
        // Se savedInstanceState != null, os campos do Material Components geralmente restauram seus estados.
        // Mas o ID precisa ser restaurado manualmente se não estiver na intent original.

        salvarButton.setOnClickListener {
            validarEEditarProduto()
        }
    }

    private fun validarEEditarProduto() {
        val nome = nomeEditText.text.toString().trim()
        val descricao = descricaoEditText.text.toString().trim()
        val preco = precoEditText.text.toString().trim()
        val imagem = imagemEditText.text.toString().trim()

        if (nome.isNotEmpty() && descricao.isNotEmpty() && preco.isNotEmpty() && imagem.isNotEmpty() && produtoId != -1) {
            editarProduto(produtoId, nome, descricao, preco, imagem)
        } else {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
        }
    }

    private fun editarProduto(id: Int, nome: String, descricao: String, preco: String, imagem: String) {
        apiService.editarProduto(id, nome, descricao, preco, imagem).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditarProdutoActivity, getString(R.string.product_updated_successfully), Toast.LENGTH_SHORT).show()
                    finish() // Voltar para a tela anterior
                } else {
                    val errorMsg = try {
                        response.errorBody()?.string() ?: getString(R.string.error_updating_product)
                    } catch (e: Exception) {
                        getString(R.string.error_updating_product)
                    }
                    Toast.makeText(this@EditarProdutoActivity, "${getString(R.string.error_updating_product)}: ${response.code()} - $errorMsg", Toast.LENGTH_LONG).show()
                    Log.e("EditarProdutoActivity", "Erro ao atualizar: ${response.code()} - $errorMsg")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                val errorMessage = getString(R.string.error_network_updating_product, t.message ?: "Erro desconhecido")
                Toast.makeText(this@EditarProdutoActivity, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("EditarProdutoActivity", "Falha na rede ao atualizar: ${t.message}", t)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_PRODUTO_ID, produtoId)
        // Os TextInputEditTexts com IDs geralmente salvam seus estados automaticamente.
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