package com.nafis.moneylaundry.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaketLaundryModel(
    val package_laundry_id: Int = 0,
    val users_id: Int = 0,
    val name: String = "",
    val price_per_kg: Int = 0,
    val description: String = "",
    val logo: String = "",
    val created_at: String = "",
    val updated_at: String = ""
) : Parcelable


