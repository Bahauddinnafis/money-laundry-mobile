package com.nafis.moneylaundry.models

data class RegisterResponse(
    val status: String,
    val message: String,
    val data: UserRegister?
)

data class UserRegister(
    val name: String,
    val email: String,
    val store_name: String,
    val store_address: String,
    val phone_number: String
)
