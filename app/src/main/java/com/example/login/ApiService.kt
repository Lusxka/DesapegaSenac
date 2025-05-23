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
    @POST("excluir_produto.php")
    fun deletarProduto(
        @Field("PRODUTO_ID") id: Int
    ): Call<Void>

    // --- NOVO MÉTODO PARA CADASTRO DE USUÁRIO ---
    @FormUrlEncoded
    @POST("cadastrar_usuario.php") // Você precisará criar este arquivo PHP
    fun registerUser(
        @Field("USUARIO_NOME") nome: String,
        @Field("USUARIO_EMAIL") email: String,
        @Field("USUARIO_SENHA") senha: String,
        @Field("USUARIO_CPF") cpf: String,
        @Field("USUARIO_TELEFONE") telefone: String
    ): Call<Void> // Ou Call<LoginResponse> se o PHP retornar dados do usuário
}