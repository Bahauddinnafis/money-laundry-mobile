package com.nafis.moneylaundry.api

import retrofit2.Call
import com.nafis.moneylaundry.models.LoginResponse
import com.nafis.moneylaundry.models.PaketLaundryModel
import com.nafis.moneylaundry.models.RegisterResponse
import com.nafis.moneylaundry.models.ResponseCreatePackage
import com.nafis.moneylaundry.models.ResponseDeletePackage
import com.nafis.moneylaundry.models.ResponseGetPackage
import com.nafis.moneylaundry.models.ResponseUpdatePackage
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

    @POST("user/package-laundry")
    fun createPackageLaundry(
        @Header("Authorization") token: String,
        @Body paketLaundryRequest: PaketLaundryModel
    ): Call<ResponseCreatePackage>

    @GET("user/package-laundry/{userId}")
    fun getPackageLaundry(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Call<ResponseGetPackage>

    @PUT("user/package-laundry/{packageLaundryId}")
    fun updatePackageLaundry(
        @Header("Authorization") token: String,
        @Path("packageLaundryId") packageLaundryId: Int,
        @Body paketLaundry: PaketLaundryModel
    ): Call<ResponseUpdatePackage>

    @DELETE("user/package-laundry/{packageLaundryId}")
    fun deletePackageLaundry(
        @Header("Authorization") token: String,
        @Path("packageLaundryId") packageLaundryId: Int
    ): Call<ResponseDeletePackage>
}