package com.nafis.moneylaundry.models.transactions

import com.google.gson.annotations.SerializedName

data class CreateOrderRequest(
    @field:SerializedName("customer_name")
    val customerName: String,

    @field:SerializedName("customer_phone_number")
    val customerPhoneNumber: String,

    @field:SerializedName("users_id")
    val usersId: Int,

    @field:SerializedName("package_laundry_id")
    val packageLaundryId: Int,

    @field:SerializedName("order_date")
    val orderDate: String,

    @field:SerializedName("pick_up_date")
    val pickUpDate: String,

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("payment_status")
    val paymentStatus: String,

    @field:SerializedName("weight")
    val weight: Int,

    @field:SerializedName("subtotal")
    val subtotal: Int,

    @field:SerializedName("subtotal_add_on_item")
    val subtotalAddOnItem: Int,

    @field:SerializedName("total_price")
    val totalPrice: Int,

    @field:SerializedName("quantity")
    val quantity: Int,

    @field:SerializedName("add_on_item")
    val addOnItem: List<AddOnItem>,
)

data class AddOnItem(
    @field:SerializedName("item_name")
    val itemName: String,

    @field:SerializedName("price_per_item")
    val pricePerItem: Int,

    @field:SerializedName("quantity")
    val quantity: Int,

    @field:SerializedName("subtotal")
    val subtotal: Int
)