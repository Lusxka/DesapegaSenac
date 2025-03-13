package com.example.login

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("login.php")
    fun fazerLogin(
        @Query("usuario") email: String,
        @Query("senha") senha: String
    ): Call<List<Usuario>>
}