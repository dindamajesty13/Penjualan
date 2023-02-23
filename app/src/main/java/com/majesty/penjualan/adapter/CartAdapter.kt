package com.majesty.penjualan.adapter

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.majesty.penjualan.R
import com.majesty.penjualan.database.SaleContract
import com.majesty.penjualan.database.SaleDbHelper
import com.majesty.penjualan.model.Cart
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.collections.ArrayList

class CartAdapter(val context: Context, listCart: ArrayList<Cart>, dbHelper: SaleDbHelper) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private var listCart: ArrayList<Cart>
    private var dbHelper: SaleDbHelper
    init {
        this.listCart = listCart
        this.dbHelper = dbHelper
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView
        var tvQuantity: TextView
        var tvPrice: TextView
        var tvDisplay: TextView
        var imgCover: ImageView
        var decrement: Button
        var increment: Button

        init {
            tvTitle = itemView.findViewById(R.id.tvLine1)
            tvQuantity = itemView.findViewById(R.id.tvLine2)
            tvPrice = itemView.findViewById(R.id.tvLine3)
            tvDisplay = itemView.findViewById(R.id.display)
            imgCover = itemView.findViewById(R.id.imgCover)
            decrement = itemView.findViewById(R.id.decrement)
            increment = itemView.findViewById(R.id.increment)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listCart.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cart = listCart[position]
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
        holder.tvDisplay.text = cart.quantity.toString()
        holder.tvQuantity.text = cart.unit
        holder.tvPrice.text = "Rp"+decimalFormat.format(totalPrice).toString()
        Glide.with(context).load(cart.image).into(holder.imgCover)
        holder.decrement.setOnClickListener {
            val quantity = Integer.parseInt(holder.tvDisplay.text.toString()) - 1
            holder.tvDisplay.text = quantity.toString()
            updateCart(cart.productCode, cart.user, quantity)
            holder.tvPrice.text = "Rp" + decimalFormat.format(updatePrice(cart.discount!!, quantity, cart.price!!)).toString()
            if (quantity == 0){
                deleteCart(cart.productCode)
            }
        }
        holder.increment.setOnClickListener {
            val quantity = Integer.parseInt(holder.tvDisplay.text.toString()) + 1
            holder.tvDisplay.text = quantity.toString()
            updateCart(cart.productCode, cart.user, quantity)
            holder.tvPrice.text = "Rp" + decimalFormat.format(updatePrice(cart.discount!!, quantity, cart.price!!)).toString()
            if (quantity == 0){
                deleteCart(cart.productCode)
            }
        }
    }

    private fun deleteCart(productCode: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete")
        builder.setMessage("Are you sure want to delete this?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes"){dialogInterface, which ->
            val db = dbHelper.writableDatabase
            db.delete(
                SaleContract.CartEntry.TABLE_NAME,
                "product_code=?",
                arrayOf(productCode)
            )
            db.close()
        }

        builder.setNegativeButton("No"){dialogInterface, which ->
            Toast.makeText(context,"Cancel checkout!",Toast.LENGTH_LONG).show()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun updateCart(productCode: String, username: String, quantity: Int) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(SaleContract.CartEntry.COLUMN_QUANTITY, quantity)
        }

        val args = arrayOf(productCode, username)
        db.update(SaleContract.CartEntry.TABLE_NAME, values, "product_code=? and user=?", args)
        db.close()
    }

    private fun updatePrice(discount: Int, quantity: Int, price: Int): Int {
        val totalPrice = if (discount > 0) {
            val discountPrice: Int = price - (discount * price / 100)
            discountPrice * quantity
        }else {
            price * quantity
        }
        return totalPrice
    }

    private fun getQuantity(productCode: String): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("select quantity from "+ SaleContract.CartEntry.TABLE_NAME + " where Product_code="+"'"+productCode+"'", null)
        if (cursor.moveToFirst())
            return cursor.getInt(cursor.getColumnIndexOrThrow(SaleContract.CartEntry.COLUMN_QUANTITY))
        return 1
    }
}