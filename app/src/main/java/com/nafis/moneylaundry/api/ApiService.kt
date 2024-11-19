package com.nafis.moneylaundry.api

import retrofit2.Call
import com.nafis.moneylaundry.models.LoginResponse
import com.nafis.moneylaundry.models.RegisterResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("user/login")
    fun loginUser(@Body loginRequest: Map<String, String>): Call<LoginResponse>

    @POST("user/register")
    fun registerUser(@Body registerRequest: Map<String, String>): Call<RegisterResponse>

    @POST("user/forgot-password/send-otp")
    fun sendOtpToEmail(@Body payload: Map<String, String>): Call<Map<String, Any>>

    @POST("user/forgot-password/verify-otp")
    fun verifyOtp(@Body payload: Map<String, String>): Call<Map<String, Any>>

    @POST("user/forgot-password/resend-otp")
    fun resendOtp(@Body payload: Map<String, String>): Call<ResponseBody>

    @POST("user/forgot-password/reset")
    fun resetPassword(@Body payload: Map<String, String>): Call<Map<String, Any>>
}