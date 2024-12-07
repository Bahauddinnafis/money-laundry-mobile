package com.nafis.moneylaundry.models.packageLaundry

import com.google.gson.annotations.SerializedName

data class ResponseDeletePackage(
    @field:SerializedName("status")
    val status: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)
