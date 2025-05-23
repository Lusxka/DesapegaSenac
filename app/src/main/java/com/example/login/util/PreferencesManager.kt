package com.example.login

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

    fun saveUser(email: String) {
        prefs.edit().putString("user_email", email).apply()
    }

    fun getUser(): String? {
        return prefs.getString("user_email", null)
    }

    fun clearUser() {
        prefs.edit().clear().apply()
    }
}
