package com.nafis.moneylaundry.models.transactions

import com.google.gson.annotations.SerializedName

data class ResponseDashboard(

	@field:SerializedName("data")
	val data: DataDashboard? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null
)

data class DataDashboard(

	@field:SerializedName("total_transaction_paid")
	val totalTransactionPaid: String? = null,

	@field:SerializedName("number_of_transactions")
	val numberOfTransactions: Int? = null
)
