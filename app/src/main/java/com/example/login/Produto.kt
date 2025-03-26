package com.example.login

import com.google.gson.annotations.SerializedName

data class Produto(
    @SerializedName("PRODUTO_ID")
    val produtoId: Int,
    @SerializedName("PRODUTO_NOME")
    val produtoNome: String,
    @SerializedName("PRODUTO_DESC")
    val produtoDescricao: String,
    @SerializedName("PRODUTO_PRECO")
    val produtoPreco: Double,
    @SerializedName("PRODUTO_DESCONTO")
    val produtoDesconto: Double,
    @SerializedName("CATEGORIA_ID")
    val categoriaId: Int,
    @SerializedName("PRODUTO_ATIVO")
    val produtoAtivo: Int
)