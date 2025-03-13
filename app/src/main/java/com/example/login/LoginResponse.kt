package com.example.login

data class LoginResponse(
    val token: String,
    val userId: String,
    val username: String
)
