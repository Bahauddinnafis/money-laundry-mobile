package com.nafis.moneylaundry.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nafis.moneylaundry.databinding.ActivityForgetPasswordBinding
import com.nafis.moneylaundry.repository.AuthRepository

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgetPasswordBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBackButton.setOnClickListener {
            finish()
        }

        binding.btnSelesai.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()

            // Validasi email
            if (email.isEmpty()) {
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kirim permintaan OTP
            authRepository.sendOtpToEmail(email) { isSuccess, message ->
                if (isSuccess) {
                    Log.d("ForgetPassword", "OTP sent successfully: $message")
                    Toast.makeText(this, "Kode OTP berhasil dikirim", Toast.LENGTH_SHORT).show()

                    // Navigasi ke ConfirmActivity
                    val intent = Intent(this, ConfirmActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                } else {
                    Log.e("ForgetPassword", "Failed to send OTP: $message")
                    Toast.makeText(this, "Gagal mengirim kode OTP: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}