package com.majesty.penjualan.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.majesty.penjualan.R
import com.majesty.penjualan.main.ProductDetailActivity
import com.majesty.penjualan.model.Product
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class ProductAdapter(val context: Context, listProduct: ArrayList<Product>) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private var listProduct: ArrayList<Product>
    init {
        this.listProduct = listProduct
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView
        var tvDiscount: TextView
        var tvPrice: TextView
        var imgCover: ImageView

        init {
            tvTitle = itemView.findViewById(R.id.tvLine1)
            tvDiscount = itemView.findViewById(R.id.tvLine2)
            tvPrice = itemView.findViewById(R.id.tvLine3)
            imgCover = itemView.findViewById(R.id.imgCover)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.items, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listProduct.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = listProduct[position]
        val discountPrice = if (product.discount > 0) {
            product.price - (product.discount * product.price / 100)
        }else {
            product.price
        }

        val symbols = DecimalFormatSymbols()
        symbols.decimalSeparator = ','
        val decimalFormat = DecimalFormat("###,###,###,###", symbols)

        holder.tvTitle.text = product.productName
        holder.tvDiscount.text = "Rp"+ decimalFormat.format(discountPrice).toString()
        if (product.discount > 0) {
            holder.tvPrice.text = "Rp" + decimalFormat.format(product.price).toString()
        }else{
            holder.tvPrice.visibility = View.INVISIBLE
        }
        Glide.with(context).load(product.image).into(holder.imgCover)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java).apply {
                putExtra("PRODUCT_CODE", product.productCode)
            }
            context.startActivity(intent)
        }
        holder.imgCover.setOnClickListener{
            val intent = Intent(context, ProductDetailActivity::class.java).apply {
                putExtra("PRODUCT_CODE", product.productCode)
            }
            context.startActivity(intent)
        }
    }
}