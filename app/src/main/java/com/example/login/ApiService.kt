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

    @FormUrlEncoded
    @POST("incluir_produto.php")
    fun incluirProduto(
        @Field("PRODUTO_NOME") nome: String,
        @Field("PRODUTO_DESC") descricao: String,
        @Field("PRODUTO_PRECO") preco: String,
        @Field("PRODUTO_IMAGEM") imagem: String
    ): Call<Void>

    @FormUrlEncoded
    @POST("editar_produto.php")
    fun editarProduto(
        @Field("PRODUTO_ID") id: Int,
        @Field("PRODUTO_NOME") nome: String,
        @Field("PRODUTO_DESC") descricao: String,
        @Field("PRODUTO_PRECO") preco: String,
        @Field("PRODUTO_IMAGEM") imagem: String
    ): Call<Void>

    @FormUrlEncoded
    @POST("excluir_produto.php")
    fun deletarProduto(
        @Field("PRODUTO_ID") id: Int
    ): Call<Void>

    @FormUrlEncoded
    @POST("cadastrar_usuario.php")
    fun registerUser(
        @Field("USUARIO_NOME") nome: String,
        @Field("USUARIO_EMAIL") email: String,
        @Field("USUARIO_SENHA") senha: String,
        @Field("USUARIO_CPF") cpf: String,
        @Field("USUARIO_TELEFONE") telefone: String
    ): Call<Void>

    @FormUrlEncoded
    @POST("editar_usuario.php") // Método para editar o usuário
    fun editarUsuario(
        @Field("USUARIO_ID") usuarioId: Int,
        @Field("USUARIO_NOME") usuarioNome: String,
        @Field("USUARIO_EMAIL") usuarioEmail: String,
        @Field("USUARIO_TELEFONE") usuarioTelefone: String,
        @Field("USUARIO_IMAGEM_URL") usuarioImagemUrl: String?
    ): Call<Void>
}