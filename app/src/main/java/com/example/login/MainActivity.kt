package com.example.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager = PreferencesManager(this)
        val userEmail = preferencesManager.getUser()

        if (userEmail != null) {
            startActivity(Intent(this, ProdutosActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish() // Evita voltar para esta tela
    }
}
