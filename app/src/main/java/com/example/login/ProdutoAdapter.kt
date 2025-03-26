package com.example.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class ProdutoAdapter(private var produtos: List<Produto>) : RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    class ProdutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeProduto: TextView = itemView.findViewById(R.id.txtNomeProduto)
        val descProduto: TextView = itemView.findViewById(R.id.txtDescProduto)
        val precoProduto: TextView = itemView.findViewById(R.id.txtPrecoProduto)
        val descontoProduto: TextView = itemView.findViewById(R.id.txtDescontoProduto)
        val ativoProduto: TextView = itemView.findViewById(R.id.txtAtivoProduto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_produto, parent, false)
        return ProdutoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val produto = produtos[position]
        holder.nomeProduto.text = "Nome: ${produto.produtoNome}"
        holder.descProduto.text = "Descrição: ${produto.produtoDescricao}"

        // Formatação do preço
        val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        val precoFormatado = formatoMoeda.format(produto.produtoPreco)
        holder.precoProduto.text = "Preço: $precoFormatado"

        val descontoFormatado = NumberFormat.getNumberInstance(Locale.getDefault()).format(produto.produtoDesconto)
        holder.descontoProduto.text = "Desconto: ${descontoFormatado}"
        holder.ativoProduto.text = "Ativo: ${if (produto.produtoAtivo == 1) "Sim" else "Não"}"
    }

    override fun getItemCount(): Int {
        return produtos.size
    }

    // Método para atualizar a lista de produtos
    fun updateProdutos(novosProdutos: List<Produto>) {
        produtos = novosProdutos
        notifyDataSetChanged()
    }
}