package com.majesty.penjualan.main

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.majesty.penjualan.R
import com.majesty.penjualan.database.SaleContract
import com.majesty.penjualan.database.SaleDbHelper
import com.majesty.penjualan.databinding.ActivityRegisterBinding
import com.majesty.penjualan.main.RegisterActivity.PreferenceHelper.customPreference
import com.majesty.penjualan.main.RegisterActivity.PreferenceHelper.name
import com.majesty.penjualan.main.RegisterActivity.PreferenceHelper.username
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class RegisterActivity : AppCompatActivity() {
    private val LOGIN_PREFERENCE: String = "login_preference"
    private lateinit var binding: ActivityRegisterBinding
    private var dbHelper: SaleDbHelper? = null
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = SaleDbHelper(applicationContext)
        insertProductToDatabase()

        prefs = customPreference(this, LOGIN_PREFERENCE)
        if (prefs!!.username.toString() != "") {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnRegister.setOnClickListener{
            goRegister()
        }
        setupLoginText()

        binding.btnEye.setOnClickListener { onEyeClick() }
        binding.btnEyePassword.setOnClickListener { onEyeClickRetype() }
    }

    private fun onEyeClickRetype() {
        if (isPasswordVisible) {
            isPasswordVisible = false
            binding.btnEyePassword.alpha = 0.25f
            binding.retypePassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.retypePassword.setSelection(binding.retypePassword.length())
        } else {
            isPasswordVisible = true
            binding.btnEyePassword.alpha = 0.75f
            binding.retypePassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.retypePassword.setSelection(binding.retypePassword.length())
        }
    }

    @SuppressLint("Recycle")
    private fun checkProductExist(productCode: String): Boolean {
        val db = dbHelper!!.readableDatabase
        val cursor = db.rawQuery("select product_code from "+ SaleContract.ProductEntry.TABLE_NAME + " where product_code="+"'"+productCode+"'", null)
        return cursor.count >= 1
    }

    private fun insertProductToDatabase() {
        val product1 = JSONObject()
        try {
            product1.put("product_code", "SKUSKILNP")
            product1.put("product_name", "So Klin Pewangi")
            product1.put("price", 15000)
            product1.put("currency", "IDR")
            product1.put("discount", 10)
            product1.put("dimension", "13 cm * 10 cm")
            product1.put("unit", "PCS")
            product1.put("image", "https://jumbo.co.id/wp-content/uploads/2020/05/249.jpg")
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        val product2 = JSONObject()
        try {
            product2.put("product_code", "GIVBIRU")
            product2.put("product_name", "Giv Biru")
            product2.put("price", 11000)
            product2.put("currency", "IDR")
            product2.put("discount", 0)
            product2.put("dimension", "13 cm * 10 cm")
            product2.put("unit", "PCS")
            product2.put("image", "https://cf.shopee.co.id/file/b79d95d7238bf634dc69880af7aa4ac5")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val product3 = JSONObject()
        try {
            product3.put("product_code", "SOKLINLQID")
            product3.put("product_name", "So Klin Liquid")
            product3.put("price", 18000)
            product3.put("currency", "IDR")
            product3.put("discount", 0)
            product3.put("dimension", "13 cm * 10 cm")
            product3.put("unit", "PCS")
            product3.put("image", "https://images.tokopedia.net/img/cache/500-square/hDjmkQ/2022/7/21/33b8d174-4f15-45db-a44f-b3e8d1fd03b0.jpg")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val product4 = JSONObject()
        try {
            product4.put("product_code", "GIVKUNING")
            product4.put("product_name", "Giv Kuning")
            product4.put("price", 10000)
            product4.put("currency", "IDR")
            product4.put("discount", 0)
            product4.put("dimension", "13 cm * 10 cm")
            product4.put("unit", "PCS")
            product4.put("image", "https://cf.shopee.co.id/file/sg-11134201-22120-9ufpa2ttxjkvb5")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonArray = JSONArray()

        jsonArray.put(product1)
        jsonArray.put(product2)
        jsonArray.put(product3)
        jsonArray.put(product4)

        val db = dbHelper!!.writableDatabase
        db.beginTransaction()

        for (index in 0 until jsonArray.length()) {
            val mJsonObject: JSONObject = jsonArray.getJSONObject(index)
            val values = ContentValues().apply {
                put(SaleContract.ProductEntry.COLUMN_PRODUCT_CODE, mJsonObject.getString("product_code"))
                put(SaleContract.ProductEntry.COLUMN_PRODUCT_NAME, mJsonObject.getString("product_name"))
                put(SaleContract.ProductEntry.COLUMN_PRODUCT_PRICE, mJsonObject.getInt("price"))
                put(SaleContract.ProductEntry.COLUMN_CURRENCY, mJsonObject.getString("currency"))
                put(SaleContract.ProductEntry.COLUMN_DISCOUNT, mJsonObject.getInt("discount"))
                put(SaleContract.ProductEntry.COLUMN_DIMENSION, mJsonObject.getString("dimension"))
                put(SaleContract.ProductEntry.COLUMN_UNIT, mJsonObject.getString("unit"))
                put(SaleContract.ProductEntry.COLUMN_IMAGE, mJsonObject.getString("image"))
            }
            if (!checkProductExist(mJsonObject.getString("product_code")))
                db?.insert(SaleContract.ProductEntry.TABLE_NAME, null, values)
        }

        db.setTransactionSuccessful()
        db.endTransaction()
    }

    private fun goRegister() {
        val name: String = binding.name.text.toString()
        val username: String = binding.username.text.toString()
        val password: String = binding.password.text.toString()
        val passwordConfirmation: String = binding.retypePassword.text.toString()

        if (!checkUserExist(username)){
            if (password != passwordConfirmation) {
                Toast.makeText(applicationContext, "Password not match!", Toast.LENGTH_SHORT).show()
            }else{
                insertToDatabase(name, username, password)
            }
        }else{
            Toast.makeText(applicationContext, "Username Already Exist", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("Recycle")
    private fun checkUserExist(username: String): Boolean {
        val db = dbHelper!!.readableDatabase
        val cursor = db.rawQuery("select user from "+ SaleContract.LoginEntry.TABLE_NAME + " where user="+"'"+username+"'", null)
        return cursor.count >= 1
    }

    private fun insertToDatabase(name: String, user: String, password: String){
        val db = dbHelper!!.writableDatabase

        val values = ContentValues().apply {
            put(SaleContract.LoginEntry.COLUMN_NAME_USER, name)
            put(SaleContract.LoginEntry.COLUMN_USERNAME, user)
            put(SaleContract.LoginEntry.COLUMN_NAME_PASSWORD, password)
        }

        db?.insert(SaleContract.LoginEntry.TABLE_NAME, null, values)
        db.close()
        prefs!!.name = name
        prefs!!.username = user
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setupLoginText() {
        if (applicationContext == null) return
        val info = resources.getString(R.string.inter_have_no_account_info)
        val register = resources.getString(R.string.login)
        val span = Spannable.Factory.getInstance().newSpannable(info + register)
        val registerClick: ClickableSpan = object : ClickableSpan() {
            override fun onClick(v: View) {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            }
        }
        val styleSpan = StyleSpan(Typeface.BOLD)
        span.setSpan(registerClick, info.length, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(
            RelativeSizeSpan(1.1f),
            info.length,
            span.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        span.setSpan(styleSpan, info.length, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(ForegroundColorSpan(Color.BLUE), info.length, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.btnLogin.text = span
        binding.btnLogin.movementMethod = LinkMovementMethod.getInstance()
    }

    private var isPasswordVisible = false

    private fun onEyeClick() {
        if (isPasswordVisible) {
            isPasswordVisible = false
            binding.btnEye.alpha = 0.25f
            binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.password.setSelection(binding.password.length())
        } else {
            isPasswordVisible = true
            binding.btnEye.alpha = 0.75f
            binding.password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.password.setSelection(binding.password.length())
        }
    }

    object PreferenceHelper {

        private const val USERNAME = "USERNAME"
        private const val NAME = "NAME"

        fun customPreference(context: Context, name: String): SharedPreferences =
            context.getSharedPreferences(name, Context.MODE_PRIVATE)

        private inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
            val editMe = edit()
            operation(editMe)
            editMe.apply()
        }

        var SharedPreferences.username
            get() = getString(USERNAME, "")
            set(value) {
                editMe {
                    it.putString(USERNAME, value)
                }
            }

        var SharedPreferences.name
            get() = getString(NAME, "")
            set(value) {
                editMe {
                    it.putString(NAME, value)
                }
            }

        var SharedPreferences.clearValues
            get() = run { }
            set(value) {
                editMe {
                    it.remove(USERNAME)
                    it.remove(NAME)
                }
            }
    }
}