package com.example.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText // Importe o TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailRegisterEditText: TextInputEditText
    private lateinit var cpfEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var passwordRegisterEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var registerUserButton: Button
    private lateinit var backToLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicialize os campos de texto e botões
        nameEditText = findViewById(R.id.nameEditText)
        emailRegisterEditText = findViewById(R.id.emailRegisterEditText)
        cpfEditText = findViewById(R.id.cpfEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        passwordRegisterEditText = findViewById(R.id.passwordRegisterEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        registerUserButton = findViewById(R.id.registerUserButton)
        backToLoginButton = findViewById(R.id.backToLoginButton)

        registerUserButton.setOnClickListener {
            attemptRegistration()
        }

        backToLoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Finaliza a RegisterActivity para que o usuário não volte para ela pressionando Voltar
        }
    }

    private fun attemptRegistration() {
        val name = nameEditText.text.toString().trim()
        val email = emailRegisterEditText.text.toString().trim()
        val cpf = cpfEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val password = passwordRegisterEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        // Validações básicas
        if (name.isEmpty() || email.isEmpty() || cpf.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_LONG).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor, insira um e-mail válido.", Toast.LENGTH_LONG).show()
            return
        }

        if (password.length < 6) { // Exemplo de validação de senha
            Toast.makeText(this, "A senha deve ter no mínimo 6 caracteres.", Toast.LENGTH_LONG).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_LONG).show()
            return
        }

        // Se todas as validações passarem, chame a API de cadastro
        performRegistration(name, email, password, cpf, phone)
    }

    private fun performRegistration(name: String, email: String, password: String, cpf: String, phone: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.113/meu_projeto_api/") // Use a mesma URL base da LoginActivity
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // Chame o método de cadastro na sua interface ApiService
        apiService.registerUser(name, email, password, cpf, phone).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Cadastro realizado com sucesso! Faça login.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish() // Finaliza a RegisterActivity
                } else {
                    // Tratar erros da API (ex: email já cadastrado, erro no servidor)
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@RegisterActivity, "Erro no cadastro: ${errorBody ?: response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}