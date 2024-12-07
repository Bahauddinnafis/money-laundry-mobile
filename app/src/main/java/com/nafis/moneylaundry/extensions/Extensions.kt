package com.nafis.moneylaundry.extensions

import android.content.Context
import com.nafis.moneylaundry.models.packageLaundry.CreatePackageRequest
import com.nafis.moneylaundry.models.packageLaundry.Data
import com.nafis.moneylaundry.models.packageLaundry.DataItem
import com.nafis.moneylaundry.models.packageLaundry.DataPackage
import com.nafis.moneylaundry.models.packageLaundry.PaketLaundryModel


fun DataItem?.toPaketLaundryModel(): PaketLaundryModel? {
    return this?.let {
        PaketLaundryModel(
            package_laundry_id = it.usersId ?: return null,
            name = it.name ?: "Unknown",
            price_per_kg = it.pricePerKg ?: 0,
            description = it.description ?: "No description",
            logo = it.logo ?: "",
            created_at = it.createdAt ?: "",
            updated_at = it.updatedAt ?: ""
        )
    }
}

fun DataPackage?.toPaketLaundryModel(): PaketLaundryModel? {
    return this?.let {
        PaketLaundryModel(
            package_laundry_id = it.packageLaundryId ?: return null,
            name = it.name ?: "Unknown",
            price_per_kg = it.pricePerKg ?: 0,
            description = it.description ?: "No description",
            logo = it.logo ?: "",
            created_at = it.createdAt ?: "",
            updated_at = it.updatedAt ?: ""
        )
    }
}

fun Data?.toPaketLaundryModel(): PaketLaundryModel {
    return this?.let {
        PaketLaundryModel(
            package_laundry_id = it.packageLaundryId ?: throw IllegalArgumentException("Package Laundry ID is null"),
            name = it.name ?: "Unknown",
            price_per_kg = it.pricePerKg ?: 0,
            description = it.description ?: "No description",
            logo = it.logo ?: "",
            created_at = it.createdAt ?: "",
            updated_at = it.updatedAt ?: ""
        )
    } ?: throw IllegalArgumentException("Data is null")
}

fun CreatePackageRequest.toPaketLaundryModel(): PaketLaundryModel {
    return PaketLaundryModel(
        name = this.name,
        price_per_kg = this.price_per_kg,
        description = this.description,
        logo = this.logo
    )
}

fun getImageResourceByName(context: Context, imageName: String): Int {
    return context.resources.getIdentifier(imageName, "drawable", context.packageName)
}