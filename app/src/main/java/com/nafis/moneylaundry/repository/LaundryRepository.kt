package com.nafis.moneylaundry.repository

import android.content.Context
import com.nafis.moneylaundry.SharedPreferencesHelper
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.extensions.toPaketLaundryModel
import com.nafis.moneylaundry.models.Data
import com.nafis.moneylaundry.models.PaketLaundryModel
import com.nafis.moneylaundry.models.ResponseCreatePackage
import com.nafis.moneylaundry.models.ResponseDeletePackage
import com.nafis.moneylaundry.models.ResponseGetPackage
import com.nafis.moneylaundry.models.ResponseUpdatePackage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LaundryRepository(private val apiClient: ApiClient, private val context: Context) {

    private val preferencesHelper = SharedPreferencesHelper(context)

    // Fungsi untuk mengambil daftar paket laundry
    fun fetchPaketLaundry(userId: Int, token: String, callback: (Result<List<PaketLaundryModel>>) -> Unit) {
        apiClient.instance.getPackageLaundry("Bearer $token", userId).enqueue(object : Callback<ResponseGetPackage> {
            override fun onResponse(call: Call<ResponseGetPackage>, response: Response<ResponseGetPackage>) {
                if (response.isSuccessful) {
                    val paketList = response.body()?.data?.mapNotNull { it.toPaketLaundryModel() } ?: emptyList()
                    callback(Result.success(paketList))
                } else {
                    callback(Result.failure(Exception("Failed to fetch package laundry: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<ResponseGetPackage>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    // Fungsi untuk membuat paket laundry baru
    fun createPackageLaundry(userId: Int, token: String, paketLaundry: PaketLaundryModel, callback: (Result<ResponseCreatePackage>) -> Unit) {
        val paketLaundryWithUserId = paketLaundry.copy(users_id = userId)

        apiClient.instance.createPackageLaundry("Bearer $token", paketLaundryWithUserId).enqueue(object : Callback<ResponseCreatePackage> {
            override fun onResponse(call: Call<ResponseCreatePackage>, response: Response<ResponseCreatePackage>) {
                if (response.isSuccessful) {
                    // Simpan package_laundry_id ke SharedPreferences
                    val newPackageId = response.body()?.data?.packageLaundryId ?: -1
                    preferencesHelper.savePackageLaundryId(newPackageId) // Simpan ID
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Failed to create package laundry: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<ResponseCreatePackage>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    // Fungsi untuk mendapatkan paket laundry berdasarkan ID
    fun getPackageLaundryById(packageLaundryId: Int, token: String, callback: (Result<PaketLaundryModel>) -> Unit) {
        // Pastikan Anda memiliki endpoint yang benar di sini
        apiClient.instance.getPackageLaundry("Bearer $token", packageLaundryId).enqueue(object : Callback<ResponseGetPackage> {
            override fun onResponse(call: Call<ResponseGetPackage>, response: Response<ResponseGetPackage>) {
                if (response.isSuccessful) {
                    val paketLaundryData = response.body()?.data?.firstOrNull { it?.packageLaundryId == packageLaundryId }
                    val paketLaundry = paketLaundryData?.toPaketLaundryModel()
                    if (paketLaundry != null) {
                        callback(Result.success(paketLaundry))
                    } else {
                        callback(Result.failure(Exception("Data not found")))
                    }
                } else {
                    callback(Result.failure(Exception("Failed to fetch package laundry: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<ResponseGetPackage>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    // Fungsi untuk memperbarui paket laundry
    fun updatePackageLaundry(packageLaundryId: Int, token: String, paketLaundry: PaketLaundryModel, callback: (Result<PaketLaundryModel>) -> Unit) {
        apiClient.instance.updatePackageLaundry("Bearer $token", packageLaundryId, paketLaundry).enqueue(object : Callback<ResponseUpdatePackage> {
            override fun onResponse(call: Call<ResponseUpdatePackage>, response: Response<ResponseUpdatePackage>) {
                if (response.isSuccessful) {
                    val updatedPaketLaundry = response.body()?.data?.toPaketLaundryModel()
                    if (updatedPaketLaundry != null) {
                        callback(Result.success(updatedPaketLaundry))
                    } else {
                        callback(Result.failure(Exception("Data not found")))
                    }
                } else {
                    callback(Result.failure(Exception("Failed to update package laundry: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<ResponseUpdatePackage>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    // Fungsi untuk menghapus paket laundry
    fun deletePackageLaundry(packageLaundryId: Int, token: String, callback: (Result<Boolean>) -> Unit) {
        apiClient.instance.deletePackageLaundry("Bearer $token", packageLaundryId).enqueue(object : Callback<ResponseDeletePackage> {
            override fun onResponse(
                call: Call<ResponseDeletePackage>,
                response: Response<ResponseDeletePackage>
            ) {
                if (response.isSuccessful) {
                    callback(Result.success(true))
                } else {
                    callback(Result.failure(Exception("Failed to delete package laundry: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<ResponseDeletePackage>, t: Throwable) {
                callback(Result.failure(t))
            }

        })
    }
}

