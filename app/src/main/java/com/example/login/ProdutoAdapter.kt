package com.example.login

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt // Importe para arredondar, se necessário

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
            nomeProduto.text = produto.produtoNome
            descProduto.text = produto.produtoDescricao

            val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

            // Preço atual (o que já está com desconto)
            precoProduto.text = formatoMoeda.format(produto.produtoPreco)

            // --- Lógica para o Desconto (valor em R$ riscado) ---
            if (produto.produtoDesconto > 0 && produto.produtoPreco > 0) { // Verifica se há desconto e preço válido
                descontoProduto.visibility = View.VISIBLE

                // CALCULA O PREÇO ORIGINAL (Se você já tiver um campo produtoPrecoOriginal, use-o diretamente)
                // Exemplo: produtoPreco = produtoPrecoOriginal * (1 - produtoDesconto/100)
                // Então: produtoPrecoOriginal = produtoPreco / (1 - produtoDesconto/100)
                val precoOriginalCalculado = produto.produtoPreco / (1.0 - (produto.produtoDesconto / 100.0))
                val precoOriginalFormatado = formatoMoeda.format(precoOriginalCalculado)

                descontoProduto.text = precoOriginalFormatado
                descontoProduto.paintFlags = descontoProduto.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                descontoProduto.setTextColor(ContextCompat.getColor(itemView.context, R.color.primary_orange))
            } else {
                descontoProduto.visibility = View.GONE
                descontoProduto.paintFlags = descontoProduto.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            // --- Lógica para o Status "Ativo" ---
            if (produto.produtoAtivo == 1) {
                ativoProduto.text = "DISPONÍVEL"
                ativoProduto.setBackgroundResource(R.drawable.rounded_status_badge)
                ativoProduto.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            } else {
                ativoProduto.text = "INDISPONÍVEL"
                ativoProduto.setBackgroundResource(R.drawable.rounded_status_unavailable_badge) // Assumindo que você criou este
                ativoProduto.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            }

            // Carrega imagem com Picasso
            produto.produtoImagem?.let { imageUrl ->
                Picasso.get().load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .error(R.drawable.ic_error_image)
                    .into(imgProduto)
            } ?: imgProduto.setImageResource(R.drawable.ic_placeholder_image)

            itemView.setOnClickListener {
                onItemClick(produto)
            }
        }
    }
}