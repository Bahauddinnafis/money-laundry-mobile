package com.nafis.moneylaundry.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nafis.moneylaundry.MainActivity
import com.nafis.moneylaundry.databinding.ActivityLoginBinding
import com.nafis.moneylaundry.repository.AuthRepository

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            // Validasi input email dan password
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email atau Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Memanggil method loginUser di AuthRepository
            authRepository.loginUser(email, password) { response ->
                if (response != null && response.status == "true" && response.data != null) {
                    // Login berhasil dan data pengguna ada
                    Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Login gagal
                    Toast.makeText(this, "Email atau Password Salah", Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity", "Login failed: $response")
                }
            }
        }

        binding.tvFogetPassword.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.tvDaftar.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}