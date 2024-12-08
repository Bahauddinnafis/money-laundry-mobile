package com.nafis.moneylaundry

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nafis.moneylaundry.auth.LoginActivity
import com.nafis.moneylaundry.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        val username = sharedPreferencesHelper.getUsername()
        val email = sharedPreferencesHelper.getEmail()
        val phoneNumber = sharedPreferencesHelper.getPhoneNumber()
        val storeName = sharedPreferencesHelper.getStoreName()
        val storeAddress = sharedPreferencesHelper.getStoreAddress()

        binding.edtUsername.setText(username)
        binding.edtEmail.setText(email)
        binding.edtNomorTelpon.setText(phoneNumber)
        binding.edtNamaToko.setText(storeName)
        binding.edtAlamatToko.setText(storeAddress)

        binding.ivBackButton.setOnClickListener {
            finish()
        }

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        sharedPreferencesHelper.clearUserData()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}