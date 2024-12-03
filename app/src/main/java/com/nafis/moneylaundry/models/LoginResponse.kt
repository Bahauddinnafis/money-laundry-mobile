package com.nafis.moneylaundry.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    val data: LoginData? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("status")
    val status: Boolean? = null,

    @SerializedName("statusCode")
    val statusCode: Int? = null
)

data class LoginData(
    @field:SerializedName("user")
    val user: UserLogin? = null,

    @field:SerializedName("token")
    val token: String? = null
)

data class UserLogin(
    @field:SerializedName("users_id")
    val users_id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("store_name")
    val storeName: String? = null,

    @SerializedName("store_address")
    val storeAddress: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("phone_number")
    val phoneNumber: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("account_status_id")
    val accountStatusId: Int? = null
)
