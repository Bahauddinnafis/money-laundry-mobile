package com.nafis.moneylaundry.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.databinding.ActivityResetPasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBackButton.setOnClickListener {
            finish()
        }

        binding.btnSelesai.setOnClickListener {
            val email = intent.getStringExtra("email") ?: ""
            val otp = intent.getStringExtra("otp") ?: ""
            val password = binding.edtPassword.text.toString().trim()
            val passwordConfirmation = binding.edtKonfirmasiPassword.text.toString().trim()

            // Validasi input
            if (password.isNotEmpty() && password == passwordConfirmation) {
                resetPassword(email, otp, password)
            } else {
                Toast.makeText(this, "Password dan konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetPassword(email: String, otp: String, password: String) {
        val apiService = ApiClient.instance
        val payload = mapOf(
            "email" to email,
            "otp" to otp,
            "password" to password,
            "password_confirmation" to password
        )

        apiService.resetPassword(payload).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ResetPasswordActivity, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@ResetPasswordActivity, "Gagal mengubah password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Toast.makeText(this@ResetPasswordActivity, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}