package com.nafis.moneylaundry.models.transactions

import com.google.gson.annotations.SerializedName

data class ResponseGetOrder(

	@field:SerializedName("data")
	val data: DataOrder? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null
)

data class CustomerOrder(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("users_id")
	val usersId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("customer_id")
	val customerId: Int? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null
)

data class AddOnItemGet(

	@field:SerializedName("price_per_item")
	val pricePerItem: Int? = null,

	@field:SerializedName("quantity")
	val quantity: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("subtotal")
	val subtotal: Int? = null,

	@field:SerializedName("add_on_item_id")
	val addOnItemId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("item_name")
	val itemName: String? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null,

	@field:SerializedName("transaction_order_id")
	val transactionOrderId: Int? = null
)

data class DataOrder(

	@field:SerializedName("total_data_transaction")
	val totalDataTransaction: Int? = null,

	@field:SerializedName("transaction_order")
	val transactionOrder: List<TransactionOrderItem?>? = null
)

data class PackageLaundry(

	@field:SerializedName("price_per_kg")
	val pricePerKg: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("users_id")
	val usersId: Int? = null,

	@field:SerializedName("logo")
	val logo: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null,

	@field:SerializedName("package_laundry_id")
	val packageLaundryId: Int? = null
)

data class TransactionOrderItem(

	@field:SerializedName("add_on_item")
	val addOnItem: List<AddOnItemGet?>? = null,

	@field:SerializedName("pick_up_date")
	val pickUpDate: String? = null,

	@field:SerializedName("quantity")
	val quantity: Int? = null,

	@field:SerializedName("total_price")
	val totalPrice: Int? = null,

	@field:SerializedName("package_laundry")
	val packageLaundry: PackageLaundryTransaction? = null,

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
	val paymentDate: Any? = null,

	@field:SerializedName("transaction_order_id")
	val transactionOrderId: Int? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("customer")
	val customer: CustomerOrder? = null
)
