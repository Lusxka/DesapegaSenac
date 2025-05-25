package com.example.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var registerButton: Button // Adicione esta linha

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferencesManager = PreferencesManager(this)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton) // Inicialize o botão de cadastro

        loginButton.setOnClickListener {
            blockLogin()
        }

        // Listener para o botão de cadastro
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java) // Abre a RegisterActivity
            startActivity(intent)
        }
    }

    private fun blockLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.113/meu_projeto_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // Note: Se o fazerLogin no ApiService espera List<Usuario>, o Callback também deve ser List<Usuario>
        // Pelo seu LoginActivity original, você estava usando List<LoginResponse> aqui.
        // Vou manter como está no seu ApiService (List<Usuario>) para consistência, mas revise
        // a resposta esperada do seu login.php
        apiService.fazerLogin(email, password).enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(
                call: Call<List<Usuario>>,
                response: Response<List<Usuario>>
            ) {
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    preferencesManager.saveUser(email)
                    val intent = Intent(this@LoginActivity, ProdutosActivity::class.java) // Assumindo ProdutosActivity é a próxima tela
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Usuário ou senha inválidos", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}