package com.nafis.moneylaundry.data

import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.models.PaketLaundryModel

object PaketLaundry {
    val allCategory = listOf(
        PaketLaundryModel(
            "Laundry Express",
            R.drawable.paket_cuci_lipat
        ),
        PaketLaundryModel(
            "Laundry Reguler",
            R.drawable.paket_cuci_setrika
        ),
        PaketLaundryModel(
            "Paket Cuci Lipat",
            R.drawable.paket_cuci_pewangi
        ),
        PaketLaundryModel(
            "Paket Cuci Kering",
            R.drawable.paket_cuci_lengkap
        ),
        PaketLaundryModel(
            "Paket Cuci Setrika",
            R.drawable.paket_cuci_lengkap
        ),
        PaketLaundryModel(
            "Paket Anti Bakteri",
            R.drawable.paket_cuci_lengkap
        ),
    )
}