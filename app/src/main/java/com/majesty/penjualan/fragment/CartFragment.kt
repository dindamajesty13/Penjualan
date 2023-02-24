package com.majesty.penjualan.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.majesty.penjualan.R
import com.majesty.penjualan.adapter.CartAdapter
import com.majesty.penjualan.database.SaleContract
import com.majesty.penjualan.database.SaleDbHelper
import com.majesty.penjualan.databinding.FragmentCartBinding
import com.majesty.penjualan.main.RegisterActivity
import com.majesty.penjualan.main.RegisterActivity.PreferenceHelper.customPreference
import com.majesty.penjualan.main.RegisterActivity.PreferenceHelper.username
import com.majesty.penjualan.model.Cart
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class CartFragment : Fragment(), CartAdapter.Callbacks{
    private lateinit var binding: FragmentCartBinding
    private var dbHelper: SaleDbHelper? = null
    private lateinit var cartList: ArrayList<Cart>
    private var prefs: SharedPreferences? = null
    private val LOGIN_PREFERENCE: String = "login_preference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = context?.let { SaleDbHelper(it) }
        prefs = customPreference(requireContext(), LOGIN_PREFERENCE)
    }

    override fun quantityChange(isChange: Boolean) {
        if (isChange) {
            getAllCartData()
            getTotalPrice()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        getAllCartData()
        getTotalPrice()

        binding.btnConfirm.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Checkout")
            builder.setMessage("Are you sure want to checkout?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            builder.setPositiveButton("Yes"){dialogInterface, which ->
                checkOut()
            }

            builder.setNegativeButton("No"){dialogInterface, which ->
                Toast.makeText(context,"Cancel checkout!",Toast.LENGTH_LONG).show()
            }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        return binding.root
    }

    private fun checkOut() {
        val db = dbHelper!!.writableDatabase
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate: String = df.format(c)

        if (cartList.isNotEmpty()) {
            for (cart in cartList) {
                val calculatePrice = if (cart.discount!! > 0) {
                    val discountPrice: Int = cart.price!! - (cart.discount!! * cart.price!! / 100)
                    discountPrice * cart.quantity
                } else {
                    cart.price!! * cart.quantity
                }
                val values = ContentValues().apply {
                    put(SaleContract.TransactionEntry.COLUMN_PRODUCT_CODE, cart.productCode)
                    put(SaleContract.TransactionEntry.COLUMN_NAME_USER, cart.user)
                    put(SaleContract.TransactionEntry.COLUMN_QUANTITY, cart.quantity)
                    put(SaleContract.TransactionEntry.COLUMN_TOTAL_PRICE, calculatePrice)
                    put(SaleContract.TransactionEntry.COLUMN_DATE, formattedDate)
                }
                db?.insert(SaleContract.TransactionEntry.TABLE_NAME, null, values)
                db.delete(
                    SaleContract.CartEntry.TABLE_NAME,
                    "product_code=?",
                    arrayOf(cart.productCode)
                )
                getAllCartData()
                getTotalPrice()
            }
            Toast.makeText(context, "successful transaction!", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "no item in card!", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

    @SuppressLint("Recycle", "NotifyDataSetChanged")
    private fun getAllCartData() {
        val db = dbHelper!!.readableDatabase
        val cursor = db.rawQuery("select * from " + SaleContract.CartEntry.TABLE_NAME + " where user="+"'"+prefs!!.username+"'", null)
        cartList = ArrayList()

        if (cursor.moveToFirst()) {
            do {
                val productCode =
                    cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.CartEntry.COLUMN_PRODUCT_CODE))
                val username =
                    cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.CartEntry.COLUMN_NAME_USER))
                val quantity =
                    cursor.getInt(cursor.getColumnIndexOrThrow(SaleContract.CartEntry.COLUMN_QUANTITY))

                getProductData(productCode, username, quantity)
            } while (cursor.moveToNext());
        }

        binding.rvCheckout.isNestedScrollingEnabled = false
        binding.rvCheckout.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = CartAdapter(context, cartList, dbHelper!!, this@CartFragment)
            adapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    getTotalPrice()
                }

            })
        }
    }

    private fun getProductData(productCode: String, user: String, quantity: Int) {
        val db = dbHelper!!.readableDatabase
        val cursor = db.rawQuery("select * from " + SaleContract.ProductEntry.TABLE_NAME + " where product_code="+"'"+productCode+"'", null)
        var productName: String? = null
        var price: Int? = null
        var discount: Int? = null
        var image: String? = null
        var unit: String? = null

        if (cursor.moveToFirst()) {
            do {
                productName =
                    cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.ProductEntry.COLUMN_PRODUCT_NAME))
                price =
                    cursor.getInt(cursor.getColumnIndexOrThrow(SaleContract.ProductEntry.COLUMN_PRODUCT_PRICE))
                discount =
                    cursor.getInt(cursor.getColumnIndexOrThrow(SaleContract.ProductEntry.COLUMN_DISCOUNT))
                image =
                    cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.ProductEntry.COLUMN_IMAGE))
                unit =
                    cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.ProductEntry.COLUMN_UNIT))
            } while (cursor.moveToNext());
        }
        val cart = Cart(productCode, user, quantity, price, discount, productName, image, unit)
        cartList.add(cart)
    }

    private fun getTotalPrice() {
        var priceProduct = 0
        var totalPrice: Int
        for (data in cartList){
            val calculatePrice = if (data.discount!! > 0) {
                val discountPrice: Int = data.price!! - (data.discount!! * data.price!! / 100)
                discountPrice * data.quantity
            }else {
                data.price!! * data.quantity
            }
            totalPrice = priceProduct + calculatePrice
            priceProduct = totalPrice
        }

        val symbols = DecimalFormatSymbols()
        symbols.decimalSeparator = ','
        val decimalFormat = DecimalFormat("###,###,###,###", symbols)

        binding.tvTotalPrice.text = "Rp" + decimalFormat.format(priceProduct).toString()
    }


}