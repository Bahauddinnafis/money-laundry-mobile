package com.nafis.moneylaundry.data
import android.os.Parcel
import android.os.Parcelable

data class AddonDetail(
    val addonName: String,
    val addonPrice: String,
    val addonQuantity: String,
    val totalPrice: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(addonName)
        parcel.writeString(addonPrice)
        parcel.writeString(addonQuantity)
        parcel.writeString(totalPrice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddonDetail> {
        override fun createFromParcel(parcel: Parcel): AddonDetail {
            return AddonDetail(parcel)
        }

        override fun newArray(size: Int): Array<AddonDetail?> {
            return arrayOfNulls(size)
        }
    }
}