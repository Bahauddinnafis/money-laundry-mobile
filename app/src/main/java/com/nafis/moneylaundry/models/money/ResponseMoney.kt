package com.nafis.moneylaundry.models.money

import com.google.gson.annotations.SerializedName

data class ResponseMoney(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null
)

data class Data(

	@field:SerializedName("total_transaction")
	val totalTransaction: List<TotalTransactionItem?>? = null,

	@field:SerializedName("transaction_order_done")
	val transactionOrderDone: Int? = null,

	@field:SerializedName("transaction_order_on_process")
	val transactionOrderOnProcess: Int? = null,

	@field:SerializedName("transaction_order_new")
	val transactionOrderNew: Int? = null,

	@field:SerializedName("total_money")
	val totalMoney: List<TotalMoneyItem?>? = null,

	@field:SerializedName("transaction_order_income")
	val transactionOrderIncome: String? = null
)

data class TotalMoneyItem(

	@field:SerializedName("order_day")
	val orderDay: String? = null,

	@field:SerializedName("total_money")
	val totalMoney: Int? = null,

	@field:SerializedName("payment_date")
	val paymentDate: String? = null
)

data class TotalTransactionItem(

	@field:SerializedName("order_date")
	val orderDate: String? = null,

	@field:SerializedName("total_transaction")
	val totalTransaction: Int? = null,

	@field:SerializedName("order_day")
	val orderDay: String? = null
)
