package com.nafis.moneylaundry.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.api.ApiService
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
                // Memastikan keyboard muncul
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        binding.btnSubmit.setOnClickListener {
            val otp = binding.pvOtp.text.toString().trim()
            val email = intent.getStringExtra("email") ?: ""

            if (otp.length == 4) { // Validasi panjang kode OTP
                verifyOtp(email, otp)
            } else {
                Toast.makeText(this, "Kode OTP harus 4 digit", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                val minutes = (timeLeftInMillis / 1000) / 60
                val seconds = (timeLeftInMillis / 1000) % 60
                binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.tvTimer.text = "Expired"
                Toast.makeText(this@ConfirmActivity, "Kode OTP telah kadaluarsa", Toast.LENGTH_SHORT).show()

                binding.btnSubmit.visibility = View.INVISIBLE

                // Menampilkan tombol untuk kirim ulang OTP
                binding.btnResendOtp.visibility = View.VISIBLE
                binding.btnResendOtp.isEnabled = true
                binding.btnResendOtp.text = "Kirim Ulang OTP"
                binding.btnResendOtp.setOnClickListener {
                    resendOtp()
                }
            }
        }.start()
    }

    private fun resendOtp() {
        // Nonaktifkan tombol resend untuk mencegah klik berulang
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
                        Toast.makeText(
                            this@ConfirmActivity,
                            "OTP telah dikirim ulang ke email Anda",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Mulai ulang timer
                        timeLeftInMillis = 300000 // 5 menit
                        startTimer()

                        // Menyembunyikan tombol resend OTP
                        binding.btnResendOtp.visibility = View.GONE

                        // Menampilkan kembali tombol submit
                        binding.btnSubmit.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(
                            this@ConfirmActivity,
                            "Gagal mengirim ulang OTP",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Aktifkan tombol resend setelah sedikit penundaan jika gagal
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
                    // Aktifkan tombol resend jika permintaan gagal
                    binding.btnResendOtp.isEnabled = true
                    binding.btnResendOtp.text = "Kirim Ulang OTP"
                }
            })
        } else {
            Toast.makeText(this, "Email tidak ditemukan", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(
                        this@ConfirmActivity,
                        "Kode OTP berhasil diverifikasi!",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@ConfirmActivity, ResetPasswordActivity::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("otp", otp)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@ConfirmActivity,
                        "Kode OTP salah atau kadaluarsa",
                        Toast.LENGTH_SHORT
                    ).show()
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

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

}