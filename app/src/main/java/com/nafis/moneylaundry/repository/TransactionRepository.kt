package com.nafis.moneylaundry.repository

import com.nafis.moneylaundry.api.ApiService
import com.nafis.moneylaundry.models.transactions.ResponseGetOrder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionRepository(private val apiService: ApiService) {

    fun getAllOrders(token: String, userId: Int, callback: (ResponseGetOrder?) -> Unit) {
        apiService.gerAllOrder(token, userId).enqueue(object : Callback<ResponseGetOrder> {
            override fun onResponse(call: Call<ResponseGetOrder>, response: Response<ResponseGetOrder>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ResponseGetOrder>, t: Throwable) {
                callback(null)
            }
        })
    }

}