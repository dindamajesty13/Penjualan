package com.majesty.penjualan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.majesty.penjualan.R
import com.majesty.penjualan.model.Cart
import com.majesty.penjualan.model.Transaction
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.collections.ArrayList

class TransactionAdapter(val context: Context, listTransaction: ArrayList<Transaction>) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private var listTransaction: ArrayList<Transaction>
    init {
        this.listTransaction = listTransaction
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView
        var tvQuantity: TextView
        var tvPrice: TextView
        var tvDate: TextView
        var imgCover: ImageView

        init {
            tvTitle = itemView.findViewById(R.id.tvLine1)
            tvQuantity = itemView.findViewById(R.id.tvLine2)
            tvPrice = itemView.findViewById(R.id.tvLine3)
            tvDate = itemView.findViewById(R.id.tvLine4)
            imgCover = itemView.findViewById(R.id.imgCover)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listTransaction.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cart = listTransaction[position]
        val totalPrice = if (cart.discount!! > 0) {
            val discountPrice: Int = cart.price!! - (cart.discount!! * cart.price!! / 100)
            discountPrice * cart.quantity
        }else {
            cart.price!! * cart.quantity
        }

        val symbols = DecimalFormatSymbols()
        symbols.decimalSeparator = ','
        val decimalFormat = DecimalFormat("###,###,###,###", symbols)

        holder.tvTitle.text = cart.productName
        holder.tvQuantity.text = cart.quantity.toString() + " " + cart.unit
        holder.tvPrice.text = "Rp"+decimalFormat.format(totalPrice).toString()
        holder.tvDate.text = cart.date.toString()
        Glide.with(context).load(cart.image).into(holder.imgCover)
    }
}