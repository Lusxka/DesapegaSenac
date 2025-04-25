package com.example.login

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EditarProdutoActivity : AppCompatActivity() {

    private lateinit var idEditText: EditText
    private lateinit var nomeEditText: EditText
    private lateinit var descricaoEditText: EditText
    private lateinit var precoEditText: EditText
    private lateinit var imagemEditText: EditText
    private lateinit var salvarButton: Button
    private lateinit var apiService: ApiService
    private var produtoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_produto)

        idEditText = findViewById(R.id.idEditText)
        nomeEditText = findViewById(R.id.nomeEditText)
        descricaoEditText = findViewById(R.id.descricaoEditText)
        precoEditText = findViewById(R.id.precoEditText)
        imagemEditText = findViewById(R.id.imagemEditText)
        salvarButton = findViewById(R.id.salvarButton)

        // Inicializar o Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.15.128/meu_projeto_api/listagem/") // Use a sua URL base correta
            .addConverterFactory(GsonConverterFactory.create())
            .client(getUnsafeOkHttpClient())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Obter os dados do produto da Intent
        produtoId = intent.getIntExtra("PRODUTO_ID", -1)
        nomeEditText.setText(intent.getStringExtra("PRODUTO_NOME"))
        descricaoEditText.setText(intent.getStringExtra("PRODUTO_DESC"))
        precoEditText.setText(intent.getStringExtra("PRODUTO_PRECO"))
        imagemEditText.setText(intent.getStringExtra("PRODUTO_IMAGEM"))
        idEditText.setText(produtoId.toString())

        salvarButton.setOnClickListener {
            val nome = nomeEditText.text.toString()
            val descricao = descricaoEditText.text.toString()
            val preco = precoEditText.text.toString()
            val imagem = imagemEditText.text.toString()

            if (nome.isNotEmpty() && descricao.isNotEmpty() && preco.isNotEmpty() && imagem.isNotEmpty() && produtoId != -1) {
                editarProduto(produtoId, nome, descricao, preco, imagem)
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editarProduto(id: Int, nome: String, descricao: String, preco: String, imagem: String) {
        apiService.editarProduto(id, nome, descricao, preco, imagem).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditarProdutoActivity, "Produto atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    finish() // Voltar para a tela de listagem
                } else {
                    Toast.makeText(this@EditarProdutoActivity, "Erro ao atualizar produto: ${response.code()} - ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@EditarProdutoActivity, "Erro de rede ao atualizar produto: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}