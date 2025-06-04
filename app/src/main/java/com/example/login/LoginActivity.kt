package com.example.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var registerButton: Button
    private lateinit var apiService: ApiService // Adicionando a instância da ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferencesManager = PreferencesManager(this)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        // Inicializa Retrofit e ApiService aqui
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.15.128/meu_projeto_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        loginButton.setOnClickListener {
            performLogin() // Renomeei e chamei a função de login
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin() { // Renomeado de blockLogin para performLogin
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        apiService.fazerLogin(email, password).enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(
                call: Call<List<Usuario>>,
                response: Response<List<Usuario>>
            ) {
                Log.d("LoginActivity", "Response Code: ${response.code()}")
                Log.d("LoginActivity", "Response Body: ${response.body()}")
                Log.d("LoginActivity", "Response Raw: ${response.raw()}")

                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val user = response.body()!![0]
                    // *** AQUI É A MUDANÇA IMPORTANTE ***
                    // Salva o objeto Usuario completo no SharedPreferences
                    preferencesManager.saveUser(user)
                    // Marca o usuário como logado
                    preferencesManager.setLoggedIn(true)

                    Toast.makeText(this@LoginActivity, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, ProdutosActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Usuário ou senha inválidos", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Log.e("LoginActivity", "Erro na chamada de login: ${t.message}")
                Toast.makeText(this@LoginActivity, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}