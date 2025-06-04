package com.example.login

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

    private val KEY_USER_ID = "user_id"
    private val KEY_USER_NAME = "user_name"
    private val KEY_USER_EMAIL = "user_email"
    private val KEY_USER_CPF = "user_cpf"
    private val KEY_USER_PHONE = "user_phone"
    private val KEY_USER_IMAGE_URL = "user_image_url"
    private val KEY_USER_ADM = "user_adm"
    private val KEY_IS_LOGGED_IN = "is_logged_in"

    fun saveUserId(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, 0)
    }

    fun saveUser(usuario: Usuario) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, usuario.usuarioId)
            putString(KEY_USER_NAME, usuario.usuarioNome)
            putString(KEY_USER_EMAIL, usuario.usuarioEmail)
            putString(KEY_USER_CPF, usuario.usuarioCpf)
            putString(KEY_USER_PHONE, usuario.usuarioTelefone)
            putString(KEY_USER_IMAGE_URL, usuario.usuarioImagemUrl)
            putString(KEY_USER_ADM, usuario.usuarioAdm.toString())
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUser(): Usuario? {
        val userId = prefs.getInt(KEY_USER_ID, 0)
        if (userId == 0) return null

        val userName = prefs.getString(KEY_USER_NAME, null) ?: return null
        val userEmail = prefs.getString(KEY_USER_EMAIL, null) ?: return null
        val userCpf = prefs.getString(KEY_USER_CPF, null) ?: return null
        val userPhone = prefs.getString(KEY_USER_PHONE, null)
        val userImageUrl = prefs.getString(KEY_USER_IMAGE_URL, null)
        val userAdm = prefs.getString(KEY_USER_ADM, "0")?.toInt() ?: 0

        return Usuario(
            usuarioId = userId,
            usuarioNome = userName,
            usuarioEmail = userEmail,
            usuarioSenha = "", // Senha nunca é salva em SharedPreferences
            usuarioCpf = userCpf,
            usuarioAdm = userAdm,
            usuarioTelefone = userPhone,
            usuarioImagemUrl = userImageUrl
        )
    }

    fun saveUserAdm(userAdm: String) {
        prefs.edit().putString(KEY_USER_ADM, userAdm).apply()
    }

    fun getUserAdm(): String? {
        return prefs.getString(KEY_USER_ADM, "0")
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}