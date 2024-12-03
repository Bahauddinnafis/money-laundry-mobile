package com.nafis.moneylaundry.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nafis.moneylaundry.SharedPreferencesHelper
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.databinding.ActivityRegisterBinding
import com.nafis.moneylaundry.models.RegisterRequest
import com.nafis.moneylaundry.models.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val apiService = ApiClient.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.edtUsername.text.toString()
            val email = binding.edtEmail.text.toString()
            val storeName = binding.edtNamaToko.text.toString()
            val storeAddress = binding.edtAlamat.text.toString()
            val password = binding.edtPassword.text.toString()
            val passwordConfirmation = binding.edtPasswordConfirmation.text.toString()
            val phoneNumber = binding.edtNomorTelpon.text.toString()

            // Membuat objek RegisterRequest untuk dikirim ke API
            val registerRequest = RegisterRequest(
                name, email, storeName, storeAddress, password, passwordConfirmation, phoneNumber
            )

            // Mengonversi RegisterRequest menjadi Map<String, String>
            val registerRequestMap = registerRequest.toMap()

            // Memanggil API untuk registrasi
            apiService.registerUser (registerRequestMap).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        if (registerResponse?.status == true) {
                            registerResponse.data?.let { data ->
                                Log.d("RegisterActivity", "Store Name: ${data.store_name}") // Akses store_name
                                Log.d("RegisterActivity", "Store Address: ${data.store_address}") // Akses store_address

                                val sharedPreferencesHelper = SharedPreferencesHelper(application)

                                sharedPreferencesHelper.saveUserId(data.users_id)
                                sharedPreferencesHelper.saveAccountStatus(data.account_status_id)
                                sharedPreferencesHelper.saveStoreName(data.store_name)
                                sharedPreferencesHelper.saveStoreAddress(data.store_address)
                                sharedPreferencesHelper.saveUsername(data.name)
                                sharedPreferencesHelper.saveEmail(data.email)
                                sharedPreferencesHelper.savePhoneNumber(data.phone_number)

                                // Log untuk memastikan data disimpan
                                Log.d("RegisterActivity", "Data stored in SharedPreferences successfully.")

                                Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Log.e("RegisterActivity", "Registration failed: ${registerResponse?.message}")
                            Log.e("RegisterActivity", "Response Body: ${response.errorBody()?.string()}")
                            Toast.makeText(this@RegisterActivity, "Registration failed: ${registerResponse?.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("RegisterActivity", "Registration failed with status code: ${response.code()}")
                        Log.e("RegisterActivity", "Response Body: ${response.errorBody()?.string()}")
                        Toast.makeText(this@RegisterActivity, "Registration failed: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
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
}
