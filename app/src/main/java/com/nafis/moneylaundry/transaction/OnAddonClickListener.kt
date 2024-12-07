package com.nafis.moneylaundry.transaction

import com.nafis.moneylaundry.data.AddonDetail

interface OnAddonClickListener {
    fun onAddonClick(addon: AddonDetail)
}