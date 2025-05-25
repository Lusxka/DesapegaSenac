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

class IncluirProdutoActivity : AppCompatActivity() {

    private lateinit var nomeEditText: EditText
    private lateinit var descricaoEditText: EditText
    private lateinit var precoEditText: EditText
    private lateinit var imagemEditText: EditText
    private lateinit var salvarButton: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incluir_produto)

        nomeEditText = findViewById(R.id.nomeEditText)
        descricaoEditText = findViewById(R.id.descricaoEditText)
        precoEditText = findViewById(R.id.precoEditText)
        imagemEditText = findViewById(R.id.imagemEditText)
        salvarButton = findViewById(R.id.salvarButton)

        // Inicializar o Retrofit (utilizando a mesma configuração insegura)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.56.1/meu_projeto_api/listagem/") // Use a sua URL base correta
            .addConverterFactory(GsonConverterFactory.create())
            .client(getUnsafeOkHttpClient())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        salvarButton.setOnClickListener {
            val nome = nomeEditText.text.toString()
            val descricao = descricaoEditText.text.toString()
            val preco = precoEditText.text.toString()
            val imagem = imagemEditText.text.toString()

            if (nome.isNotEmpty() && descricao.isNotEmpty() && preco.isNotEmpty() && imagem.isNotEmpty()) {
                incluirNovoProduto(nome, descricao, preco, imagem)
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun incluirNovoProduto(nome: String, descricao: String, preco: String, imagem: String) {
        apiService.incluirProduto(nome, descricao, preco, imagem).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@IncluirProdutoActivity, "Produto incluído com sucesso!", Toast.LENGTH_SHORT).show()
                    finish() // Voltar para a tela de listagem
                } else {
                    Toast.makeText(this@IncluirProdutoActivity, "Erro ao incluir produto: ${response.code()} - ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@IncluirProdutoActivity, "Erro de rede ao incluir produto: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}