package com.majesty.penjualan.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.majesty.penjualan.adapter.ProductAdapter
import com.majesty.penjualan.adapter.SearchAdapter
import com.majesty.penjualan.database.SaleContract
import com.majesty.penjualan.database.SaleDbHelper
import com.majesty.penjualan.databinding.ActivitySearchBinding
import com.majesty.penjualan.model.Product

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private var dbHelper: SaleDbHelper? = null
    private lateinit var productList: ArrayList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = SaleDbHelper(applicationContext)

        val productQuery = intent.getStringExtra("PRODUCT").toString()

        getProductByQuery(productQuery)
    }

    private fun getProductByQuery(productQuery: String) {
        val db = dbHelper!!.readableDatabase
        productList = ArrayList()

        val cursor = db.query(true, SaleContract.ProductEntry.TABLE_NAME, arrayOf(
            SaleContract.ProductEntry.COLUMN_PRODUCT_CODE,
            SaleContract.ProductEntry.COLUMN_PRODUCT_NAME,
            SaleContract.ProductEntry.COLUMN_PRODUCT_PRICE,
            SaleContract.ProductEntry.COLUMN_CURRENCY,
            SaleContract.ProductEntry.COLUMN_DISCOUNT,
            SaleContract.ProductEntry.COLUMN_DIMENSION,
            SaleContract.ProductEntry.COLUMN_UNIT,
            SaleContract.ProductEntry.COLUMN_IMAGE,
        ), SaleContract.ProductEntry.COLUMN_PRODUCT_NAME + " LIKE ?", arrayOf<String>("%$productQuery%"), null, null, null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val productCode =
                    cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.ProductEntry.COLUMN_PRODUCT_CODE))
                val productName =
                    cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.ProductEntry.COLUMN_PRODUCT_NAME))
                val price =
                    cursor.getInt(cursor.getColumnIndexOrThrow(SaleContract.ProductEntry.COLUMN_PRODUCT_PRICE))
                val currency =
                    cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.ProductEntry.COLUMN_CURRENCY))
                val discount =
                    cursor.getInt(cursor.getColumnIndexOrThrow(SaleContract.ProductEntry.COLUMN_DISCOUNT))
                val dimension =
                    cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.ProductEntry.COLUMN_DIMENSION))
                val unit =
                    cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.ProductEntry.COLUMN_UNIT))
                val image =
                    cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.ProductEntry.COLUMN_IMAGE))

                val product = Product(productCode, productName, price, currency, discount, dimension, unit, image)
                productList.add(product)

            } while (cursor.moveToNext());
        }
        binding.rvSearch.isNestedScrollingEnabled = false
        binding.rvSearch.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(context)
            // set the custom adapter to the RecyclerView
            adapter = SearchAdapter(context, productList)
        }
        cursor.close()
    }
}