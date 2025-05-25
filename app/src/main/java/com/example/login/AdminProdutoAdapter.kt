package com.example.login

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog // Importa AlertDialog do androidx
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Interface ProdutoCallback atualizada com onProdutoNaoDeletado
interface ProdutoCallback {
    fun onProdutoDeletado()
    fun onProdutoNaoDeletado(mensagemErro: String)
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
        viewHolder.preco.text = "R$ ${String.format("%.2f", produto.produtoPreco)}" // Formata o preço com 2 casas decimais

        // Carrega imagem com Picasso, incluindo placeholders e tratamento de erro
        produto.produtoImagem?.let {
            Picasso.get().load(it)
                .placeholder(R.drawable.ic_placeholder_image) // Imagem placeholder (crie esta drawable)
                .error(R.drawable.ic_error_image) // Imagem de erro (crie esta drawable)
                .into(viewHolder.imagem)
        } ?: run {
            // Se produto.produtoImagem for nulo, define o placeholder diretamente
            viewHolder.imagem.setImageResource(R.drawable.ic_placeholder_image)
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
            val context = viewHolder.itemView.context // Obtenha o contexto da view

            // Cria o AlertDialog para confirmação de exclusão
            AlertDialog.Builder(context)
                .setTitle("Confirmar Exclusão")
                .setMessage("Tem certeza que deseja excluir o produto '${produto.produtoNome}'? Esta ação é irreversível.")
                .setPositiveButton("Sim, Excluir") { dialog, which ->
                    // Se o usuário clicar "Sim", prossegue com a exclusão
                    apiService.deletarProduto(produto.produtoId).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Produto deletado com sucesso!", Toast.LENGTH_LONG).show()
                                callback.onProdutoDeletado() // Chama o callback de sucesso na Activity
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Erro desconhecido"
                                Toast.makeText(context, "Erro ao deletar o produto: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                                callback.onProdutoNaoDeletado("Código: ${response.code()}, Erro: $errorBody") // Passa erro detalhado
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            val errorMessage = t.message ?: "Erro de conexão"
                            Toast.makeText(context, "Erro ao deletar o produto: $errorMessage", Toast.LENGTH_LONG).show()
                            callback.onProdutoNaoDeletado("Rede: $errorMessage") // Passa erro de rede
                        }
                    })
                }
                .setNegativeButton("Não, Cancelar") { dialog, which ->
                    // Se o usuário clicar "Não", apenas fecha o diálogo
                    dialog.dismiss()
                    Toast.makeText(context, "Exclusão cancelada.", Toast.LENGTH_SHORT).show()
                }
                .show() // Exibe o AlertDialog
        }
    }

    override fun getItemCount() = dataSet.size

    /**
     * Atualiza a lista de produtos no adapter e notifica o RecyclerView para redesenhar.
     * @param newProdutos A nova lista de produtos.
     */
    fun updateData(newProdutos: List<Produto>) {
        this.dataSet = newProdutos
        notifyDataSetChanged() // Informa ao RecyclerView que os dados foram alterados
    }
}