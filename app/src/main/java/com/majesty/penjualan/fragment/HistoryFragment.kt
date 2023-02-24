package com.majesty.penjualan.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.majesty.penjualan.adapter.TransactionAdapter
import com.majesty.penjualan.database.SaleContract
import com.majesty.penjualan.database.SaleDbHelper
import com.majesty.penjualan.databinding.FragmentHistoryBinding
import com.majesty.penjualan.main.RegisterActivity
import com.majesty.penjualan.main.RegisterActivity.PreferenceHelper.customPreference
import com.majesty.penjualan.main.RegisterActivity.PreferenceHelper.username
import com.majesty.penjualan.model.Transaction
import kotlin.collections.ArrayList

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private var dbHelper: SaleDbHelper? = null
    private lateinit var transactionList: ArrayList<Transaction>
    private var prefs: SharedPreferences? = null
    private val LOGIN_PREFERENCE: String = "login_preference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = context?.let { SaleDbHelper(it) }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        prefs = customPreference(requireContext(), LOGIN_PREFERENCE)

        getAllHistoryData()

        return binding.root
    }

    private fun getAllHistoryData() {
        val db = dbHelper!!.readableDatabase
        val cursor = db.rawQuery("select * from " + SaleContract.TransactionEntry.TABLE_NAME + " where user="+"'"+prefs!!.username+"'", null)
        transactionList = ArrayList()

        if (cursor.moveToFirst()) {
            do {
                val productCode =
                    cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.TransactionEntry.COLUMN_PRODUCT_CODE))
                val username =
                    cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.TransactionEntry.COLUMN_NAME_USER))
                val quantity =
                    cursor.getInt(cursor.getColumnIndexOrThrow(SaleContract.TransactionEntry.COLUMN_QUANTITY))
                val totalPrice =
                    cursor.getInt(cursor.getColumnIndexOrThrow(SaleContract.TransactionEntry.COLUMN_TOTAL_PRICE))
                val date =
                    cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.TransactionEntry.COLUMN_DATE))

                getProductData(productCode, username, quantity, date)

            } while (cursor.moveToNext());
        }

        binding.rvCheckout.isNestedScrollingEnabled = false
        binding.rvCheckout.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = TransactionAdapter(context, transactionList)
        }
    }

    private fun getProductData(productCode: String, user: String, quantity: Int, date: String) {
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
        val transaction = Transaction(productCode, user, quantity, price, discount, productName, image, date,
            unit!!
        )
        transactionList.add(transaction)
    }
}