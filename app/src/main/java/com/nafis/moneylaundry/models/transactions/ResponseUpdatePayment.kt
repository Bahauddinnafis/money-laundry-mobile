package com.nafis.moneylaundry.models.transactions

import com.google.gson.annotations.SerializedName

data class ResponseUpdatePayment(

	@field:SerializedName("data")
	val data: DataPayment? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null
)

data class DataPayment(

	@field:SerializedName("pick_up_date")
	val pickUpDate: String? = null,

	@field:SerializedName("quantity")
	val quantity: Int? = null,

	@field:SerializedName("total_price")
	val totalPrice: Int? = null,

	@field:SerializedName("payment_status")
	val paymentStatus: String? = null,

	@field:SerializedName("weight")
	val weight: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null,

	@field:SerializedName("order_date")
	val orderDate: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("subtotal")
	val subtotal: Int? = null,

	@field:SerializedName("subtotal_add_on_item")
	val subtotalAddOnItem: Int? = null,

	@field:SerializedName("users_id")
	val usersId: Int? = null,

	@field:SerializedName("customer_id")
	val customerId: Int? = null,

	@field:SerializedName("package_laundry_id")
	val packageLaundryId: Int? = null,

	@field:SerializedName("payment_date")
	val paymentDate: String? = null,

	@field:SerializedName("transaction_order_id")
	val transactionOrderId: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
)
