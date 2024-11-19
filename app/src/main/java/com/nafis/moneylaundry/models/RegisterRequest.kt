package com.nafis.moneylaundry.models

data class RegisterRequest(
    val name: String,
    val email: String,
    val storeName: String,
    val storeAddress: String,
    val password: String,
    val passwordConfirmation: String,
    val phoneNumber: String
) {
    fun toMap(): Map<String, String> {
        return mapOf(
            "name" to name,
            "email" to email,
            "store_name" to storeName,
            "store_address" to storeAddress,
            "password" to password,
            "password_confirmation" to passwordConfirmation,
            "phone_number" to phoneNumber
        )
    }
}