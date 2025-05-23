package com.example.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

class ProdutoAdapter(
    private var produtos: List<Produto>,
    private val onItemClick: (Produto) -> Unit
) : RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_produto, parent, false)
        return ProdutoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val produto = produtos[position]
        holder.bind(produto, onItemClick)
    }

    override fun getItemCount(): Int {
        return produtos.size
    }

    fun updateProdutos(novosProdutos: List<Produto>) {
        produtos = novosProdutos
        notifyDataSetChanged()
    }

    class ProdutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduto: ImageView = itemView.findViewById(R.id.imgProduto)
        val nomeProduto: TextView = itemView.findViewById(R.id.txtNomeProduto)
        val descProduto: TextView = itemView.findViewById(R.id.txtDescProduto)
        val precoProduto: TextView = itemView.findViewById(R.id.txtPrecoProduto)
        val descontoProduto: TextView = itemView.findViewById(R.id.txtDescontoProduto)
        val ativoProduto: TextView = itemView.findViewById(R.id.txtAtivoProduto)

        fun bind(produto: Produto, onItemClick: (Produto) -> Unit) {
            nomeProduto.text = "Nome: ${produto.produtoNome}"
            descProduto.text = "Descrição: ${produto.produtoDescricao}"

            val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            val precoFormatado = formatoMoeda.format(produto.produtoPreco)
            precoProduto.text = "Preço: $precoFormatado"

            val descontoFormatado = NumberFormat.getNumberInstance(Locale.getDefault()).format(produto.produtoDesconto)
            descontoProduto.text = "Desconto: ${descontoFormatado}"
            ativoProduto.text = "Ativo: ${if (produto.produtoAtivo == 1) "Sim" else "Não"}"

            produto.produtoImagem?.let { imageUrl -> // Renomeado para imageUrl para clareza
                // Carrega a URL completa da imagem diretamente
                Picasso.get().load(imageUrl)
                    .placeholder(R.drawable.placeholder_product)
                    .error(R.drawable.placeholder_product)
                    .into(imgProduto)
            } ?: imgProduto.setImageResource(R.drawable.placeholder_product)

            itemView.setOnClickListener {
                onItemClick(produto)
            }
        }
    }
}