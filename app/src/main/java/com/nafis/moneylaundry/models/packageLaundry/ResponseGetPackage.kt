package com.nafis.moneylaundry.models.packageLaundry

import com.google.gson.annotations.SerializedName

data class ResponseGetPackage(

    @field:SerializedName("data")
	val data: List<DataItem?>? = null,

    @field:SerializedName("message")
	val message: String? = null,

    @field:SerializedName("status")
	val status: Boolean? = null,

    @field:SerializedName("statusCode")
	val statusCode: Int? = null
)

data class DataItem(

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
