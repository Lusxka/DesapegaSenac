package com.example.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

class ProdutoDetailBottomSheet : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ProdutoDetailBottomSheet"
        private const val ARG_PRODUTO = "produto_object"

        fun newInstance(produto: Produto): ProdutoDetailBottomSheet {
            val fragment = ProdutoDetailBottomSheet()
            val args = Bundle().apply {
                putSerializable(ARG_PRODUTO, produto)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val produto = arguments?.getSerializable(ARG_PRODUTO) as? Produto

        if (produto != null) {
            view.findViewById<TextView>(R.id.productDetailName).text = produto.produtoNome
            view.findViewById<TextView>(R.id.productDetailDescription).text = produto.produtoDescricao

            val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            view.findViewById<TextView>(R.id.productDetailPrice).text = formatoMoeda.format(produto.produtoPreco)

            val productImageView = view.findViewById<ImageView>(R.id.productDetailImage)
            produto.produtoImagem?.let { imageUrl ->
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_product)
                    .error(R.drawable.placeholder_product)
                    .into(productImageView)
            } ?: productImageView.setImageResource(R.drawable.placeholder_product)

            val contactButton = view.findViewById<Button>(R.id.contactSellerButton)
            contactButton.setOnClickListener {
                val phoneNumber = "5511911029671"
                val uri = Uri.parse("https://wa.me/$phoneNumber")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "WhatsApp não instalado.", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }
}