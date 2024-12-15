package com.nafis.moneylaundry.repository

import android.util.Log
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.models.auth.LoginResponse
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
                    callback(Result.success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Log.e("LoginError", "Error: $errorMessage")
                    callback(Result.failure(Exception("Login gagal: ${response.code()} - $errorMessage")))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginFailure", "Request failed", t)
                callback(Result.failure(Exception("Koneksi gagal: ${t.localizedMessage}")))
            }
        })
    }

    fun sendOtpToEmail(email: String, callback: (Boolean, String) -> Unit) {
        val payload = mapOf("email" to email)

        ApiClient.instance.sendOtpToEmail(payload).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    // Mengambil nilai `status` sebagai Boolean
                    val status = responseBody?.get("status") as? Boolean == true
                    val message = responseBody?.get("message") as? String ?: "Unknown error"

                    if (status) {
                        callback(true, message)
                    } else {
                        callback(false, message)
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