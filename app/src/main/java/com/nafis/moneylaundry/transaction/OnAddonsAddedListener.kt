package com.nafis.moneylaundry.transaction

import com.nafis.moneylaundry.data.AddonDetail

interface OnAddonsAddedListener {
    fun onAddonsAdded(addonName: String, addonPrice: String, addonQuantity: String, totalPrice: String)
    fun onAddonUpdated(addon: AddonDetail, addonName: String, addonPrice: String, addonQuantity: String, totalPrice: String)
}