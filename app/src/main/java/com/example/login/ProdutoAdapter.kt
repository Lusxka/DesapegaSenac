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
        holder.nomeProduto.text = "Nome: ${produto.PRODUTO_NOME}"
        holder.descProduto.text = "Descrição: ${produto.PRODUTO_DESC}"

        // Formatação do preço
        val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        val precoFormatado = formatoMoeda.format(produto.PRODUTO_PRECO)
        holder.precoProduto.text = "Preço: $precoFormatado"

        holder.descontoProduto.text = "Desconto: ${produto.PRODUTO_DESCONTO}%"
        holder.ativoProduto.text = "Ativo: ${if (produto.PRODUTO_ATIVO == 1) "Sim" else "Não"}"
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
