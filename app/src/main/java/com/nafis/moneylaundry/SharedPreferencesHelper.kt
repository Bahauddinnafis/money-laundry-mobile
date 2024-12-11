package com.nafis.moneylaundry

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nafis.moneylaundry.data.AddonDetail

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("token", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }

    fun saveUserId(userId: Int) {
        sharedPreferences.edit().putInt("userId", userId).apply()
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt("userId", -1)
    }

    fun saveAccountStatus(status: Int) {
        sharedPreferences.edit().putInt("account_status", status).apply()
    }

    fun getAccountStatus(): Int {
        val status = sharedPreferences.getInt("account_status", -1)
        Log.d("SharedPreferencesHelper", "getAccountStatus: $status")
        return status
    }

    fun savePackageLaundryId(packageLaundryId: Int) {
        sharedPreferences.edit().putInt("package_laundry_id", packageLaundryId).apply()
    }

    fun getPackageLaundryId(): Int {
        return sharedPreferences.getInt("package_laundry_id", -1)
    }

    fun saveLogoUrl(logoUrl: String) {
        sharedPreferences.edit().putString("logo_url", logoUrl).apply()
    }

    fun getLogoUrl(): String? {
        return sharedPreferences.getString("logo_url", null)
    }

    fun saveStoreName(storeName: String) {
        sharedPreferences.edit().putString("storeName", storeName).apply()
    }

    fun saveStoreAddress(storeAddress: String) {
        sharedPreferences.edit().putString("storeAddress", storeAddress).apply()
    }

    fun saveUsername(username: String) {
        sharedPreferences.edit().putString("username", username).apply()
    }

    fun saveEmail(email: String) {
        sharedPreferences.edit().putString("email", email).apply()
    }

    fun savePhoneNumber(phoneNumber: String) {
        sharedPreferences.edit().putString("phone_number", phoneNumber).apply()
    }

    fun savePackageName(packageName: String) {
        sharedPreferences.edit().putString("package_name", packageName).apply()
    }

    fun savePackageDescription(packageDescription: String) {
        sharedPreferences.edit().putString("package_description", packageDescription).apply()
    }

    fun getStoreName(): String? {
        return sharedPreferences.getString("storeName", null)
    }

    fun getStoreAddress(): String? {
        return sharedPreferences.getString("storeAddress", null)
    }

    fun getUsername(): String? {
        return sharedPreferences.getString("username", null)
    }

    fun getEmail(): String? {
        return sharedPreferences.getString("email", null)
    }

    fun getPhoneNumber(): String? {
        return sharedPreferences.getString("phone_number", null)
    }

    fun savePricePerKg(pricePerKg: Int) {
        sharedPreferences.edit().putInt("price_per_kg", pricePerKg).apply()
    }

    fun getPricePerKg(): Int {
        return sharedPreferences.getInt("price_per_kg", 0)
    }

    fun getPackageName(): String? {
        return sharedPreferences.getString("package_name", null)
    }

    fun getPackageDescription(): String? {
        return sharedPreferences.getString("package_description", null)
    }

    fun saveCustomerName(customerName: String) {
        sharedPreferences.edit().putString("customer_name", customerName).apply()
    }

    fun getCustomerName(): String? {
        return sharedPreferences.getString("customer_name", null)
    }

    fun saveCustomerPhoneNumber(phoneNumber: String) {
        sharedPreferences.edit().putString("customer_phone_number", phoneNumber).apply()
    }

    fun getCustomerPhoneNumber(): String? {
        return sharedPreferences.getString("customer_phone_number", null)
    }

    fun saveWeight(weight: Int) {
        sharedPreferences.edit().putInt("weight", weight).apply()
    }

    fun getWeight(): Int {
        return sharedPreferences.getInt("weight", 0)
    }

    fun saveOrderDate(orderDate: String) {
        sharedPreferences.edit().putString("order_date", orderDate).apply()
    }

    fun getOrderDate(): String? {
        return sharedPreferences.getString("order_date", null)
    }

    fun savePickUpDate(pickUpDate: String) {
        sharedPreferences.edit().putString("pick_up_date", pickUpDate).apply()
    }

    fun getPickUpDate(): String? {
        return sharedPreferences.getString("pick_up_date", null)
    }

    fun saveTotalPrice(totalPrice: Int) {
        sharedPreferences.edit().putInt("total_price", totalPrice).apply()
    }

    fun getTotalPrice(): Int {
        return sharedPreferences.getInt("total_price", 0)
    }

    fun saveSubtotal(subtotal: Int) {
        sharedPreferences.edit().putInt("subtotal", subtotal).apply()
    }

    fun getSubtotal(): Int {
        return sharedPreferences.getInt("subtotal", 0)
    }

    fun saveSubtotalAddOnItem(subtotalAddOnItem: Int) {
        sharedPreferences.edit().putInt("subtotal_addon_item", subtotalAddOnItem).apply()
    }

    fun getSubtotalAddOnItem(): Int {
        return sharedPreferences.getInt("subtotal_addon_item", 0)
    }

    fun saveQuantity(quantity: Int) {
        return sharedPreferences.edit().putInt("quantity", quantity).apply()
    }

    fun getQuantity(): Int {
        return sharedPreferences.getInt("quantity", 0)
    }

    fun savePaymentStatus(paymentStatus: String) {
        return sharedPreferences.edit().putString("payment_status", paymentStatus).apply()
    }

    fun getPaymentStatus(): String? {
        return sharedPreferences.getString("payment_status", null)
    }

    fun saveAddons(addons: List<AddonDetail>) {
        val gson = Gson()
        val json = gson.toJson(addons)
        sharedPreferences.edit().putString("addons", json).apply()
    }

    fun getAddons(): List<AddonDetail> {
        val gson = Gson()
        val json = sharedPreferences.getString("addons", null)
        val type = object : TypeToken<List<AddonDetail>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun saveTransactionOrderId(transactionOrderId: Int) {
        sharedPreferences.edit().putInt("transaction_order_id", transactionOrderId).apply()
    }

    fun getTransactionOrderId(): Int {
        return sharedPreferences.getInt("transaction_order_id", -1)
    }
}


