package com.nafis.moneylaundry

import android.content.Context
import android.util.Log

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
        return sharedPreferences.getInt("package_laundry_id", -1) // Mengembalikan -1 jika tidak ada
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
}


