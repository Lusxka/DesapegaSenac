package com.example.login

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("login.php")
    fun fazerLogin(
        @Query("usuario") email: String,
        @Query("senha") senha: String
    ): Call<List<Usuario>>

    @GET("produtos.php")
    fun getProdutos(): Call<List<Produto>>

    // Método para incluir um produto
    @FormUrlEncoded
    @POST("incluir_produto.php")
    fun incluirProduto(
        @Field("PRODUTO_NOME") nome: String,
        @Field("PRODUTO_DESC") descricao: String,
        @Field("PRODUTO_PRECO") preco: String,
        @Field("PRODUTO_IMAGEM") imagem: String
    ): Call<Void>

    // Método para editar um produto
    @FormUrlEncoded
    @POST("editar_produto.php")
    fun editarProduto(
        @Field("PRODUTO_ID") id: Int,
        @Field("PRODUTO_NOME") nome: String,
        @Field("PRODUTO_DESC") descricao: String,
        @Field("PRODUTO_PRECO") preco: String,
        @Field("PRODUTO_IMAGEM") imagem: String
    ): Call<Void>

    // Método para deletar um produto
    @FormUrlEncoded
    @POST("deletar_produto.php")
    fun deletarProduto(
        @Field("PRODUTO_ID") id: Int
    ): Call<Void>

}