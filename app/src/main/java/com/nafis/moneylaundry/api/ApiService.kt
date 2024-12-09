package com.nafis.moneylaundry.api

import retrofit2.Call
import com.nafis.moneylaundry.models.auth.LoginResponse
import com.nafis.moneylaundry.models.packageLaundry.PaketLaundryModel
import com.nafis.moneylaundry.models.auth.RegisterResponse
import com.nafis.moneylaundry.models.packageLaundry.ResponseCreatePackage
import com.nafis.moneylaundry.models.packageLaundry.ResponseDeletePackage
import com.nafis.moneylaundry.models.packageLaundry.ResponseGetPackage
import com.nafis.moneylaundry.models.packageLaundry.ResponseUpdatePackage
import com.nafis.moneylaundry.models.transactions.CreateOrderRequest
import com.nafis.moneylaundry.models.transactions.ResponseCreateOrder
import com.nafis.moneylaundry.models.transactions.ResponseDashboard
import com.nafis.moneylaundry.models.transactions.ResponseGetOrder
import com.nafis.moneylaundry.models.transactions.ResponseSendInvoice
import com.nafis.moneylaundry.models.transactions.ResponseTransactionOrder
import com.nafis.moneylaundry.models.transactions.ResponseUpdatePayment
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

    @POST("user/transaction-order")
    fun createOrder(
        @Header("Authorization") token: String,
        @Body createOrderRequest: CreateOrderRequest
    ): Call<ResponseCreateOrder>

    @GET("user/transaction-order/{userId}")
    fun gerAllOrder(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Call<ResponseGetOrder>

    @GET("user/transaction-order-detail/{transactionOrderId}")
    fun getTransactionOrder(
        @Header("Authorization") token: String,
        @Path("transactionOrderId") transactionOrderId: Int
    ): Call<ResponseTransactionOrder>

    @PUT("user/update-payment/{transactionOrderId}")
    fun updatePayment(
        @Header("Authorization") token: String,
        @Path("transactionOrderId") transactionOrderId: Int,
        @Body requestBody: Map<String, String>
    ): Call<ResponseUpdatePayment>

    @POST("user/send-invoice-wa")
    fun sendInvoiceWA(
        @Header("Authorization") token: String,
        @Body requestBody: Map<String, Int>
    ): Call<ResponseSendInvoice>

    @GET("user/dashboard/{userId}")
    fun getDashboardData(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Call<ResponseDashboard>
}