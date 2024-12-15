package com.nafis.moneylaundry.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

            if (password.isNotEmpty() && password == passwordConfirmation) {
                resetPassword(email, otp, password)
            } else {
                showCustomToastError(this, "Password dan konfirmasi password tidak cocok")
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
                    showCustomToastSuccess(this@ResetPasswordActivity, "Password berhasil diubah")
                    startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
                    finish()
                } else {
                    showCustomToastError(this@ResetPasswordActivity, "Gagal mengubah password")
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Toast.makeText(this@ResetPasswordActivity, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InflateParams")
    fun showCustomToastSuccess(activity: AppCompatActivity, message: String) {
        val inflater = activity.layoutInflater
        val layout: View = inflater.inflate(R.layout.custom_toast_success, null)

        val icon = layout.findViewById<ImageView>(R.id.toast_icon)
        val text = layout.findViewById<TextView>(R.id.toast_message)
        text.text = message
        icon.setImageResource(R.drawable.ic_check_circle)

        val toast = Toast(activity.applicationContext)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InflateParams")
    fun showCustomToastError(activity: AppCompatActivity, message: String) {
        val inflater = activity.layoutInflater
        val layout: View = inflater.inflate(R.layout.custom_toast_error, null)

        val icon = layout.findViewById<ImageView>(R.id.toast_icon)
        val text = layout.findViewById<TextView>(R.id.toast_message)
        text.text = message
        icon.setImageResource(R.drawable.ic_cancel)

        val toast = Toast(activity.applicationContext)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()
    }
}