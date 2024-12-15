package com.nafis.moneylaundry.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nafis.moneylaundry.R
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

            if (email.isEmpty()) {
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                showCustomToastError(this, "Email tidak boleh kosong")
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showCustomToastError(this, "Email tidak valid")
                return@setOnClickListener
            }

            authRepository.sendOtpToEmail(email) { isSuccess, message ->
                if (isSuccess) {
                    Log.d("ForgetPassword", "OTP sent successfully: $message")
                    showCustomToastSuccess(this, "Kode OTP berhasil dikirim")

                    val intent = Intent(this, ConfirmActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                } else {
                    Log.e("ForgetPassword", "Failed to send OTP: $message")
                    showCustomToastError(this, "Gagal mengirim kode OTP")
                }
            }
        }
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