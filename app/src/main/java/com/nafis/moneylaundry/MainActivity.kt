package com.nafis.moneylaundry

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.nafis.moneylaundry.auth.LoginActivity
import com.nafis.moneylaundry.databinding.ActivityMainBinding
import com.nafis.moneylaundry.fragments.PaketLaundryFragment
import com.nafis.moneylaundry.fragments.HomeFragment
import com.nafis.moneylaundry.fragments.MoneyFragment
import com.nafis.moneylaundry.fragments.DaftarTransaksiFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.progressBar

        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        val token = sharedPreferencesHelper.getToken()

        if (token.isNullOrEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            replaceFragment(HomeFragment())
        }

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.packageLaundry -> replaceFragment(PaketLaundryFragment())
                R.id.transaction -> replaceFragment(DaftarTransaksiFragment())
                R.id.money -> replaceFragment(MoneyFragment())
                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        showProgressBar()

        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()

        hideProgressBar()
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }
}