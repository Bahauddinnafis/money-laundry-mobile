package com.nafis.moneylaundry.models

data class RegisterResponse(
    val status: Boolean,
    val statusCode: Int,
    val message: String,
    val data: UserRegister?
)

data class RegisterData(
    val token: String?,
    val user: UserRegister?
)

data class UserRegister(
    val users_id: Int,
    val name: String,
    val store_name: String,
    val store_address: String,
    val email: String,
    val phone_number: String,
    val account_status_id: Int,
    val created_at: String,
    val updated_at: String
)
