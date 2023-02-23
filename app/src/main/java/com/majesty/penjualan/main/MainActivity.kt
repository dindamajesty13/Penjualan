package com.majesty.penjualan.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.majesty.penjualan.R
import com.majesty.penjualan.databinding.ActivityMainBinding
import com.majesty.penjualan.fragment.CartFragment
import com.majesty.penjualan.fragment.HistoryFragment
import com.majesty.penjualan.fragment.HomeFragment
import com.majesty.penjualan.main.RegisterActivity.PreferenceHelper.clearValues
import com.majesty.penjualan.main.RegisterActivity.PreferenceHelper.customPreference

class MainActivity : AppCompatActivity() {
    private val LOGIN_PREFERENCE: String = "login_preference"
    private lateinit var binding: ActivityMainBinding
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadFragment(HomeFragment())

        prefs = customPreference(this, LOGIN_PREFERENCE)

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.cart -> {
                    loadFragment(CartFragment())
                    true
                }
                R.id.history -> {
                    loadFragment(HistoryFragment())
                    true
                }
                else -> {false}
            }
        }
    }

    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout -> {
                prefs!!.clearValues
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}