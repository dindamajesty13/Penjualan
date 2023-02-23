package com.majesty.penjualan.main

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.majesty.penjualan.database.SaleContract
import com.majesty.penjualan.database.SaleDbHelper
import com.majesty.penjualan.databinding.ActivityProductDetailBinding
import com.majesty.penjualan.main.RegisterActivity.PreferenceHelper.customPreference
import com.majesty.penjualan.main.RegisterActivity.PreferenceHelper.username

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    private var dbHelper: SaleDbHelper? = null
    private var prefs: SharedPreferences? = null
    private val LOGIN_PREFERENCE: String = "login_preference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = SaleDbHelper(applicationContext)
        prefs = customPreference(this, LOGIN_PREFERENCE)

        val productCode = intent.getStringExtra("PRODUCT_CODE").toString()

        getProductDetail(productCode)
    }

    @SuppressLint("SetTextI18n")
    private fun getProductDetail(productCode: String) {
        val db = dbHelper!!.readableDatabase
        val cursor = db.rawQuery("select * from " + SaleContract.ProductEntry.TABLE_NAME + " where product_code="+"'"+productCode+"'", null)
        if (cursor.moveToFirst()) {
            val discount = cursor.getInt(cursor.getColumnIndexOrThrow(SaleContract.ProductEntry.COLUMN_DISCOUNT))
            val price = cursor.getInt(cursor.getColumnIndexOrThrow(SaleContract.ProductEntry.COLUMN_PRODUCT_PRICE))
            val totalPrice = if (discount > 0) {
                price - (discount * price / 100)
            }else {
                price
            }

            Glide.with(applicationContext).load(cursor.getString(cursor.getColumnIndexOrThrow(
                SaleContract.ProductEntry.COLUMN_IMAGE))).into(binding.imageView)
            binding.title.text = cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.ProductEntry.COLUMN_PRODUCT_NAME))
            binding.price.text = price.toString()
            binding.dimension.text = cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.ProductEntry.COLUMN_DIMENSION))
            binding.discount.text = "$discount%"
            binding.totalPrice.text = totalPrice.toString()

            val username = prefs!!.username
            binding.btnBuy.setOnClickListener{
                if (!checkProductExistInCart(productCode)) {
                    insertToCart(productCode, username!!, 1)
                } else {
                    updateCart(productCode, username!!, getQuantity(productCode))
                }
            }
        }

    }

    private fun getQuantity(productCode: String): Int {
        val db = dbHelper!!.readableDatabase
        val cursor = db.rawQuery("select quantity from "+ SaleContract.CartEntry.TABLE_NAME + " where Product_code="+"'"+productCode+"'", null)
        if (cursor.moveToFirst())
            return cursor.getInt(cursor.getColumnIndexOrThrow(SaleContract.CartEntry.COLUMN_QUANTITY))
        return 1
    }

    private fun updateCart(productCode: String, username: String, quantity: Int) {
        val db = dbHelper!!.writableDatabase

        val values = ContentValues().apply {
            put(SaleContract.CartEntry.COLUMN_QUANTITY, quantity + 1)
        }

        val args = arrayOf(productCode, username)
        db.update(SaleContract.CartEntry.TABLE_NAME, values, "product_code=? and user=?", args)
        db.close()
        Toast.makeText(applicationContext, "Add product to cart success", Toast.LENGTH_SHORT).show()
    }

    private fun checkProductExistInCart(productCode: String): Boolean {
        val db = dbHelper!!.readableDatabase
        val cursor = db.rawQuery("select product_code from "+ SaleContract.CartEntry.TABLE_NAME + " where Product_code="+"'"+productCode+"'", null)
        return cursor.count >= 1
    }

    private fun insertToCart(productCode: String, username: String, quantity: Int) {
        val db = dbHelper!!.writableDatabase

        val values = ContentValues().apply {
            put(SaleContract.CartEntry.COLUMN_PRODUCT_CODE, productCode)
            put(SaleContract.CartEntry.COLUMN_NAME_USER, username)
            put(SaleContract.CartEntry.COLUMN_QUANTITY, quantity)
        }

        db?.insert(SaleContract.CartEntry.TABLE_NAME, null, values)
        db.close()
        Toast.makeText(applicationContext, "Add product to cart success", Toast.LENGTH_SHORT).show()
    }
}