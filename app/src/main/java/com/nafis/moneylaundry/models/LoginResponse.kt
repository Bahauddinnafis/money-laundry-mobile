package com.nafis.moneylaundry.models

data class AuthResponse(
    val status: Boolean,
    val statusCode: Int,
    val message: String,
    val data: LoginData
)

data class LoginData(
    val token: String,
    val user: User
)

data class User(
    val id: Int,
    val name: String,
    val email: String
)
