package com.example.login

data class Usuario(
    val usuarioId: Int,
    val usuarioNome: String,
    val usuarioEmail: String,
    val usuarioSenha: String,
    val usuarioCpf: String,
    val usuarioAdm: Int
)