package com.nafis.moneylaundry.models.auth


data class LoginRequest(
    val email: String,
    val password: String
)