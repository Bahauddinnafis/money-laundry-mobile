package com.nafis.moneylaundry.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.databinding.ActivityConfirmBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmBinding
    private lateinit var timer: CountDownTimer
    private var timeLeftInMillis: Long = 300000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBackButton.setOnClickListener {
            finish()
        }

        startTimer()

        binding.pvOtp.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        binding.btnSubmit.setOnClickListener {
            val otp = binding.pvOtp.text.toString().trim()
            val email = intent.getStringExtra("email") ?: ""

            if (otp.length == 4) {
                verifyOtp(email, otp)
            } else {
                showCustomToastError(this, "Kode OTP harus 4 digit")
            }
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                val minutes = (timeLeftInMillis / 1000) / 60
                val seconds = (timeLeftInMillis / 1000) % 60
                binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding.tvTimer.text = "Expired"
                showCustomToastError(this@ConfirmActivity, "Kode OTP telah kadaluarsa")

                binding.btnSubmit.visibility = View.INVISIBLE

                binding.btnResendOtp.visibility = View.VISIBLE
                binding.btnResendOtp.isEnabled = true
                binding.btnResendOtp.text = "Kirim Ulang OTP"
                binding.btnResendOtp.setOnClickListener {
                    resendOtp()
                }
            }
        }.start()
    }

    @SuppressLint("SetTextI18n")
    private fun resendOtp() {
        binding.btnResendOtp.isEnabled = false
        binding.btnResendOtp.text = "Menunggu..."

        val email = intent.getStringExtra("email") ?: ""
        if (email.isNotEmpty()) {
            val apiService = ApiClient.instance
            val payload = mapOf("email" to email)

            apiService.resendOtp(payload).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        showCustomToastSuccess(this@ConfirmActivity, "OTP telah dikirim ulang ke email Anda")

                        timeLeftInMillis = 300000
                        startTimer()

                        binding.btnResendOtp.visibility = View.GONE

                        binding.btnSubmit.visibility = View.VISIBLE
                    } else {
                        showCustomToastError(this@ConfirmActivity, "Gagal mengirim ulang OTP")
                        binding.btnResendOtp.isEnabled = true
                        binding.btnResendOtp.text = "Kirim Ulang OTP"
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        this@ConfirmActivity,
                        "Terjadi kesalahan: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.btnResendOtp.isEnabled = true
                    binding.btnResendOtp.text = "Kirim Ulang OTP"
                }
            })
        } else {
            showCustomToastError(this, "Email tidak ditemukan")
        }
    }

    private fun verifyOtp(email: String, otp: String) {
        val apiService = ApiClient.instance
        val payload = mapOf("email" to email, "otp" to otp)

        apiService.verifyOtp(payload).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                if (response.isSuccessful) {
                    showCustomToastSuccess(this@ConfirmActivity, "Kode OTP berhasil diverifikasi!")
                    val intent = Intent(this@ConfirmActivity, ResetPasswordActivity::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("otp", otp)
                    startActivity(intent)
                } else {
                    showCustomToastError(this@ConfirmActivity, "Kode OTP salah atau kadaluarsa")
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Toast.makeText(
                    this@ConfirmActivity,
                    "Terjadi kesalahan: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
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

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

}