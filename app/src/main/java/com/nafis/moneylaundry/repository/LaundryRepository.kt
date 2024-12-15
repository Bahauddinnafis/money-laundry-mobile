package com.nafis.moneylaundry.repository

import android.content.Context
import com.nafis.moneylaundry.SharedPreferencesHelper
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.extensions.toPaketLaundryModel
import com.nafis.moneylaundry.models.packageLaundry.PaketLaundryModel
import com.nafis.moneylaundry.models.packageLaundry.ResponseCreatePackage
import com.nafis.moneylaundry.models.packageLaundry.ResponseDeletePackage
import com.nafis.moneylaundry.models.packageLaundry.ResponseGetPackage
import com.nafis.moneylaundry.models.packageLaundry.ResponseUpdatePackage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LaundryRepository(private val apiClient: ApiClient, context: Context) {

    private val preferencesHelper = SharedPreferencesHelper(context)

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

    fun createPackageLaundry(userId: Int, token: String, paketLaundry: PaketLaundryModel, callback: (Result<ResponseCreatePackage>) -> Unit) {
        val paketLaundryWithUserId = paketLaundry.copy(users_id = userId)

        apiClient.instance.createPackageLaundry("Bearer $token", paketLaundryWithUserId).enqueue(object : Callback<ResponseCreatePackage> {
            override fun onResponse(call: Call<ResponseCreatePackage>, response: Response<ResponseCreatePackage>) {
                if (response.isSuccessful) {
                    val newPackageId = response.body()?.data?.packageLaundryId ?: -1
                    preferencesHelper.savePackageLaundryId(newPackageId)
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


