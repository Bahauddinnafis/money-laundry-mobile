package com.nafis.moneylaundry.repository

import android.util.Log
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.models.LoginResponse
import com.nafis.moneylaundry.models.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {
    fun loginUser(email: String, password: String, callback: (Result<LoginResponse>) -> Unit) {
        val loginRequest = mapOf("email" to email, "password" to password)

        ApiClient.instance.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("LoginResponse", "Response: ${response.body()}")
                    // Jika berhasil, kirimkan hasil sukses melalui Result
                    callback(Result.success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Log.e("LoginError", "Error: $errorMessage")
                    // Kirim error yang lebih spesifik
                    callback(Result.failure(Exception("Login gagal: ${response.code()} - $errorMessage")))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginFailure", "Request failed", t)
                // Kirim error koneksi
                callback(Result.failure(Exception("Koneksi gagal: ${t.localizedMessage}")))
            }
        })
    }

    fun registerUser(registerData: Map<String, String>, callback: (RegisterResponse?) -> Unit) {
        ApiClient.instance.registerUser(registerData).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("RegisterResponse", "Response: ${response.body()}")
                    callback(response.body())
                } else {
                    Log.e("RegisterError", "Error: ${response.errorBody()?.string()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("RegisterFailure", "Request failed", t)
                callback(null)
            }
        })
    }

    // Fungsi untuk mengirimkan kode OTP ke email
    fun sendOtpToEmail(email: String, callback: (Boolean, String) -> Unit) {
        val payload = mapOf("email" to email)

        ApiClient.instance.sendOtpToEmail(payload).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    // Mengambil nilai `status` sebagai Boolean
                    val status = responseBody?.get("status") as? Boolean ?: false
                    val message = responseBody?.get("message") as? String ?: "Unknown error"

                    if (status) {
                        callback(true, message) // Sukses
                    } else {
                        callback(false, message) // Gagal
                    }
                } else {
                    callback(false, "Server error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                callback(false, "Failed to connect to server: ${t.localizedMessage}")
            }
        })
    }
}