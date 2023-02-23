package com.majesty.penjualan.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.majesty.penjualan.database.SaleContract
import com.majesty.penjualan.database.SaleDbHelper
import com.majesty.penjualan.adapter.ProductAdapter
import com.majesty.penjualan.databinding.FragmentHomeBinding
import com.majesty.penjualan.main.ProductDetailActivity
import com.majesty.penjualan.main.RegisterActivity
import com.majesty.penjualan.main.RegisterActivity.PreferenceHelper.customPreference
import com.majesty.penjualan.main.RegisterActivity.PreferenceHelper.name
import com.majesty.penjualan.main.RegisterActivity.PreferenceHelper.username
import com.majesty.penjualan.main.SearchActivity
import com.majesty.penjualan.model.Product

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var dbHelper: SaleDbHelper? = null
    private lateinit var productList: ArrayList<Product>
    private val LOGIN_PREFERENCE: String = "login_preference"
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = context?.let { SaleDbHelper(it) }
        prefs = context?.let { customPreference(it, LOGIN_PREFERENCE) }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.textTitle.text = "Hello, "+ prefs!!.name

        getAllDataProduct()

        binding.imgSearch.setOnClickListener { search() }

        return binding.root
    }

    private fun search() {
        val query = binding.editText.text.toString()

        val intent = Intent(context, SearchActivity::class.java).apply {
            putExtra("PRODUCT", query)
        }
        startActivity(intent)
        binding.editText.text.clear()
    }

    fun getAllDataProduct() {
        val db = dbHelper!!.readableDatabase
        val cursor = db.rawQuery("select * from " + SaleContract.ProductEntry.TABLE_NAME, null)
        productList = ArrayList()

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
        binding.rvItems.isNestedScrollingEnabled = false
        binding.rvItems.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            // set the custom adapter to the RecyclerView
            adapter = ProductAdapter(context, productList)
        }

        binding.rvRecommendation.isNestedScrollingEnabled = false
        binding.rvRecommendation.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = GridLayoutManager(activity, 3)
            // set the custom adapter to the RecyclerView
            adapter = ProductAdapter(context, productList)
        }
        cursor.close()
    }
}