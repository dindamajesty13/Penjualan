package com.majesty.penjualan.adapter

import android.annotation.SuppressLint
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

class SearchAdapter(val context: Context, listProduct: ArrayList<Product>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var listProduct: ArrayList<Product>
    init {
        this.listProduct = listProduct
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView
        var tvDiscount: TextView
        var tvPrice: TextView
        var tvTotalPrice: TextView
        var imgCover: ImageView

        init {
            tvTitle = itemView.findViewById(R.id.tvLine1)
            tvDiscount = itemView.findViewById(R.id.tvLine2)
            tvPrice = itemView.findViewById(R.id.tvLine3)
            tvTotalPrice = itemView.findViewById(R.id.tvLine4)
            imgCover = itemView.findViewById(R.id.imgCover)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listProduct.size
    }

    @SuppressLint("SetTextI18n")
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
        holder.tvPrice.text = product.discount.toString() + "%"
        holder.tvDiscount.text = "Rp" + decimalFormat.format(product.price).toString()
        holder.tvTotalPrice.text = "Rp" + decimalFormat.format(discountPrice).toString()
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