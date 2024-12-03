package com.nafis.moneylaundry.models

data class CreatePackageRequest(
    val users_id: Int,
    val name: String,
    val price_per_kg: Int,
    val description: String,
    val logo: String
)
