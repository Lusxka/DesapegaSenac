package com.example.login

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface ProdutoCallback {
    fun onProdutoDeletado()
}

class AdminProdutoAdapter(
    private var dataSet: List<Produto>,
    private val apiService: ApiService,
    private val callback: ProdutoCallback
) : RecyclerView.Adapter<AdminProdutoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.nomeAdminProduto)
        val descricao: TextView = view.findViewById(R.id.descricaoAdminProduto)
        val preco: TextView = view.findViewById(R.id.precoAdminProduto)
        val imagem: ImageView = view.findViewById(R.id.imagemAdminProduto)
        val editarButton: Button = view.findViewById(R.id.editarAdminButton)
        val deletarButton: Button = view.findViewById(R.id.deletarAdminButton)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_admin_produto, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val produto = dataSet[position]
        viewHolder.nome.text = produto.produtoNome
        viewHolder.descricao.text = produto.produtoDescricao
        viewHolder.preco.text = "R$ ${produto.produtoPreco}"
        produto.produtoImagem?.let {
            Picasso.get().load(it).into(viewHolder.imagem)
        }

        viewHolder.editarButton.setOnClickListener {
            val intent = Intent(it.context, EditarProdutoActivity::class.java)
            intent.putExtra("PRODUTO_ID", produto.produtoId)
            intent.putExtra("PRODUTO_NOME", produto.produtoNome)
            intent.putExtra("PRODUTO_DESC", produto.produtoDescricao)
            intent.putExtra("PRODUTO_PRECO", produto.produtoPreco.toString())
            produto.produtoImagem?.let { intent.putExtra("PRODUTO_IMAGEM", it) }
            it.context.startActivity(intent)
        }

        viewHolder.deletarButton.setOnClickListener {
            apiService.deletarProduto(produto.produtoId).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(viewHolder.itemView.context, "Produto deletado com sucesso!", Toast.LENGTH_LONG).show()
                        callback.onProdutoDeletado()
                    } else {
                        Toast.makeText(viewHolder.itemView.context, "Erro ao deletar o produto", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(viewHolder.itemView.context, "Erro ao deletar o produto: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    override fun getItemCount() = dataSet.size

    fun updateList(newList: List<Produto>) {
        dataSet = newList
        notifyDataSetChanged()
    }
}
