package com.nafis.moneylaundry.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.SharedPreferencesHelper
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.databinding.ActivityRegisterBinding
import com.nafis.moneylaundry.models.auth.RegisterRequest
import com.nafis.moneylaundry.models.auth.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val apiService = ApiClient.instance
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.progressBar

        binding.btnRegister.setOnClickListener {
            val name = binding.edtUsername.text.toString()
            val email = binding.edtEmail.text.toString()
            val storeName = binding.edtNamaToko.text.toString()
            val storeAddress = binding.edtAlamat.text.toString()
            val password = binding.edtPassword.text.toString()
            val passwordConfirmation = binding.edtPasswordConfirmation.text.toString()
            val phoneNumber = binding.edtNomorTelpon.text.toString()

            progressBar.visibility = View.VISIBLE

            val registerRequest = RegisterRequest(
                name, email, storeName, storeAddress, password, passwordConfirmation, phoneNumber
            )

            val registerRequestMap = registerRequest.toMap()

            apiService.registerUser (registerRequestMap).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        if (registerResponse?.status == true) {
                            registerResponse.data?.let { data ->
                                Log.d("RegisterActivity", "Store Name: ${data.store_name}")
                                Log.d("RegisterActivity", "Store Address: ${data.store_address}")

                                val sharedPreferencesHelper = SharedPreferencesHelper(application)

                                sharedPreferencesHelper.saveUserId(data.users_id)
                                sharedPreferencesHelper.saveAccountStatus(data.account_status_id)
                                sharedPreferencesHelper.saveStoreName(data.store_name)
                                sharedPreferencesHelper.saveStoreAddress(data.store_address)
                                sharedPreferencesHelper.saveUsername(data.name)
                                sharedPreferencesHelper.saveEmail(data.email)
                                sharedPreferencesHelper.savePhoneNumber(data.phone_number)

                                Log.d("RegisterActivity", "Data stored in SharedPreferences successfully.")

                                showCustomToastSuccess(this@RegisterActivity, "Registrasi Berhasil")
                                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Log.e("RegisterActivity", "Registration failed: ${registerResponse?.message}")
                            Log.e("RegisterActivity", "Response Body: ${response.errorBody()?.string()}")
                            showCustomToastError(this@RegisterActivity, "Registrasi Gagal!")
                        }
                    } else {
                        Log.e("RegisterActivity", "Registration failed with status code: ${response.code()}")
                        Log.e("RegisterActivity", "Response Body: ${response.errorBody()?.string()}")
                        showCustomToastError(this@RegisterActivity, "Registrasi Gagal!")
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Log.e("RegisterActivity", "Error: ${t.message}")
                }
            })
        }

        binding.tvMasuk.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
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
