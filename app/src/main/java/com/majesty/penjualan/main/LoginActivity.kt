package com.majesty.penjualan.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.majesty.penjualan.R
import com.majesty.penjualan.database.SaleContract
import com.majesty.penjualan.database.SaleDbHelper
import com.majesty.penjualan.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var dbHelper: SaleDbHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = SaleDbHelper(applicationContext)

        binding.btnLogin.setOnClickListener{goLogin()}
        setupRegisterText()
    }

    private fun goLogin() {
        val username: String = binding.username.text.toString()
        val password: String = binding.password.text.toString()

        checkUserLoggedIn(username, password)
    }

    private fun checkUserLoggedIn(username: String, password: String) {
        val db = dbHelper!!.readableDatabase
        val cursor = db.rawQuery("select * from "+ SaleContract.LoginEntry.TABLE_NAME + " where user="+"'"+username+"' LIMIT 1", null)
        if (cursor.count == 1) {
            if (cursor.moveToFirst()) {
                val userUsername =
                    cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.LoginEntry.COLUMN_NAME_USER))
                val userPassword =
                    cursor.getString(cursor.getColumnIndexOrThrow(SaleContract.LoginEntry.COLUMN_NAME_PASSWORD))
                if (username == userUsername) {
                    if (password == userPassword) {
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(applicationContext, "Wrong Password", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Username Doesn't Exist", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }else{
            Toast.makeText(applicationContext, "Username Doesn't Exist", Toast.LENGTH_SHORT)
                .show()
        }
        cursor.close()
    }

    private fun setupRegisterText() {
        if (applicationContext == null) return
        val info = resources.getString(R.string.inter_have_an_account)
        val register = resources.getString(R.string.sign_up)
        val span = Spannable.Factory.getInstance().newSpannable(info + register)
        val registerClick: ClickableSpan = object : ClickableSpan() {
            override fun onClick(v: View) {
                startActivity(Intent(applicationContext, RegisterActivity::class.java))
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
        binding.btnSignUp.text = span
        binding.btnSignUp.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun getColorAttr(context: Context, attrId: Int): Int {
        val typedValue = TypedValue()
        val themes = context.theme
        themes.resolveAttribute(attrId, typedValue, true)
        return typedValue.data
    }
}