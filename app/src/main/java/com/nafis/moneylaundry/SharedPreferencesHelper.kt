package com.nafis.moneylaundry

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nafis.moneylaundry.data.AddonDetail
import com.nafis.moneylaundry.models.transactions.ResponseCreateOrder
import com.nafis.moneylaundry.models.transactions.ResponseTransactionOrder

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    // Fungsi untuk menyimpan token
    fun saveToken(token: String) {
        sharedPreferences.edit().putString("token", token).apply()
    }

    // Fungsi untuk mengambil token
    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    // Fungsi untuk membersihkan data pengguna
    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }

    // Fungsi untuk menyimpan userId
    fun saveUserId(userId: Int) {
        sharedPreferences.edit().putInt("userId", userId).apply()
    }

    // Fungsi untuk mengambil userId
    fun getUserId(): Int {
        return sharedPreferences.getInt("userId", -1)
    }

    // Fungsi untuk menyimpan status akun
    fun saveAccountStatus(status: Int) {
        sharedPreferences.edit().putInt("account_status", status).apply()
    }

    // Fungsi untuk mengambil status akun
    fun getAccountStatus(): Int {
        val status = sharedPreferences.getInt("account_status", -1)
        Log.d("SharedPreferencesHelper", "getAccountStatus: $status")
        return status
    }

    // Fungsi untuk menyimpan package_laundry_id
    fun savePackageLaundryId(packageLaundryId: Int) {
        sharedPreferences.edit().putInt("package_laundry_id", packageLaundryId).apply()
    }

    // Fungsi untuk mengambil package_laundry_id
    fun getPackageLaundryId(): Int {
        return sharedPreferences.getInt("package_laundry_id", -1)
    }

    // Fungsi untuk menyimpan URL logo
    fun saveLogoUrl(logoUrl: String) {
        sharedPreferences.edit().putString("logo_url", logoUrl).apply()
    }

    // Fungsi untuk mengambil URL logo
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

    // Fungsi untuk menyimpan harga per kg
    fun savePricePerKg(pricePerKg: Int) {
        sharedPreferences.edit().putInt("price_per_kg", pricePerKg).apply()
    }

    // Fungsi untuk mengambil harga per kg
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

    // Fungsi untuk mengambil nama pelanggan
    fun getCustomerName(): String? {
        return sharedPreferences.getString("customer_name", null)
    }

    // Fungsi untuk menyimpan nomor telepon pelanggan
    fun saveCustomerPhoneNumber(phoneNumber: String) {
        sharedPreferences.edit().putString("customer_phone_number", phoneNumber).apply()
    }

    // Fungsi untuk mengambil nomor telepon pelanggan
    fun getCustomerPhoneNumber(): String? {
        return sharedPreferences.getString("customer_phone_number", null)
    }

    // Fungsi untuk menyimpan berat
    fun saveWeight(weight: Int) {
        sharedPreferences.edit().putInt("weight", weight).apply()
    }

    // Fungsi untuk mengambil berat
    fun getWeight(): Int {
        return sharedPreferences.getInt("weight", 0)
    }

    // Fungsi untuk menyimpan tanggal pesanan
    fun saveOrderDate(orderDate: String) {
        sharedPreferences.edit().putString("order_date", orderDate).apply()
    }

    // Fungsi untuk mengambil tanggal pesanan
    fun getOrderDate(): String? {
        return sharedPreferences.getString("order_date", null)
    }
    // Fungsi untuk menyimpan tanggal pengambilan
    fun savePickUpDate(pickUpDate: String) {
        sharedPreferences.edit().putString("pick_up_date", pickUpDate).apply()
    }

    // Fungsi untuk mengambil tanggal pengambilan
    fun getPickUpDate(): String? {
        return sharedPreferences.getString("pick_up_date", null)
    }

    // Fungsi untuk menyimpan total harga
    fun saveTotalPrice(totalPrice: Int) {
        sharedPreferences.edit().putInt("total_price", totalPrice).apply()
    }

    // Fungsi untuk mengambil total harga
    fun getTotalPrice(): Int {
        return sharedPreferences.getInt("total_price", 0)
    }
    // Fungsi untuk menyimpan subtotal
    fun saveSubtotal(subtotal: Int) {
        sharedPreferences.edit().putInt("subtotal", subtotal).apply()
    }

    // Fungsi untuk mengambil subtotal
    fun getSubtotal(): Int {
        return sharedPreferences.getInt("subtotal", 0)
    }

    // Fungsi untuk menyimpan subtotal add-on
    fun saveSubtotalAddOnItem(subtotalAddOnItem: Int) {
        sharedPreferences.edit().putInt("subtotal_addon_item", subtotalAddOnItem).apply()
    }

    // Fungsi untuk mengambil subtotal add-on
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

    // Fungsi untuk menyimpan addons
    fun saveAddons(addons: List<AddonDetail>) {
        val gson = Gson()
        val json = gson.toJson(addons)
        sharedPreferences.edit().putString("addons", json).apply()
    }

    // Fungsi untuk mengambil addons
    fun getAddons(): List<AddonDetail> {
        val gson = Gson()
        val json = sharedPreferences.getString("addons", null)
        val type = object : TypeToken<List<AddonDetail>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    // Fungsi untuk menyimpan transaction_order_id
    fun saveTransactionOrderId(transactionOrderId: Int) {
        sharedPreferences.edit().putInt("transaction_order_id", transactionOrderId).apply()
    }

    // Fungsi untuk mengambil transaction_order_id
    fun getTransactionOrderId(): Int {
        return sharedPreferences.getInt("transaction_order_id", -1)
    }
}


