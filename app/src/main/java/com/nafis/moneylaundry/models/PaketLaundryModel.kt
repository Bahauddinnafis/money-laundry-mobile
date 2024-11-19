package com.nafis.moneylaundry.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaketLaundryModel(
    val name: String,
    val photo: Int
) : Parcelable
