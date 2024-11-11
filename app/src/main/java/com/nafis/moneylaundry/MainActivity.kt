package com.nafis.moneylaundry

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.nafis.moneylaundry.databinding.ActivityMainBinding
import com.nafis.moneylaundry.fragments.PaketLaundryFragment
import com.nafis.moneylaundry.fragments.HomeFragment
import com.nafis.moneylaundry.fragments.MoneyFragment
import com.nafis.moneylaundry.fragments.DaftarTransaksiFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.cart -> replaceFragment(PaketLaundryFragment())
                R.id.note -> replaceFragment(DaftarTransaksiFragment())
                R.id.money -> replaceFragment(MoneyFragment())

                else -> {

                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}