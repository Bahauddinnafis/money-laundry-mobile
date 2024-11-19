package com.nafis.moneylaundry.models

data class LoginResponse(
    val status: String,
    val message: String,
    val data: UserLogin?
)

data class UserLogin(
    val id: Int,
    val name: String?,
    val email: String?,
)