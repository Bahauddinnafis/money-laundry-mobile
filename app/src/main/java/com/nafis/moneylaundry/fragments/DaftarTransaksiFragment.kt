package com.nafis.moneylaundry.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nafis.moneylaundry.SharedPreferencesHelper
import com.nafis.moneylaundry.adapter.DaftarTransaksiRecyclerview
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.databinding.FragmentDaftarTransaksiBinding
import com.nafis.moneylaundry.models.transactions.ResponseGetOrder
import com.nafis.moneylaundry.models.transactions.ResponseTransactionOrder
import com.nafis.moneylaundry.models.transactions.TransactionOrderItem
import com.nafis.moneylaundry.transaction.OrderStatusActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DaftarTransaksiFragment : Fragment() {
    private var _binding: FragmentDaftarTransaksiBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DaftarTransaksiRecyclerview
    private val transaksiList = mutableListOf<TransactionOrderItem?>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDaftarTransaksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        fetchTransactionData()

        binding.ivBackButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView() {
        recyclerView = binding.rvDaftarTransaksi
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        adapter = DaftarTransaksiRecyclerview(transaksiList) { transactionOrderId ->
            fetchTransactionOrderDetail(transactionOrderId)
        }
        recyclerView.adapter = adapter
    }

    private fun fetchTransactionOrderDetail(transactionOrderId: Int) {
        val sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val token = "Bearer ${sharedPreferencesHelper.getToken()}"

        ApiClient.instance.getTransactionOrder(token, transactionOrderId).enqueue(object : Callback<ResponseTransactionOrder> {
            override fun onResponse(call: Call<ResponseTransactionOrder>, response: Response<ResponseTransactionOrder>) {
                if (response.isSuccessful) {
                    val intent = Intent(requireContext(), OrderStatusActivity::class.java)
                    intent.putExtra("transactionOrderId", transactionOrderId)
                    startActivity(intent)
                } else {
                    Log.e("FetchTransactionError", "Error: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Gagal mengambil detail transaksi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseTransactionOrder>, t: Throwable) {
                Toast.makeText(requireContext(), "Kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchTransactionData() {
        val sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val userId = sharedPreferencesHelper.getUserId()
        val token = "Bearer ${sharedPreferencesHelper.getToken()}"

        ApiClient.instance.gerAllOrder(token, userId).enqueue(object : Callback<ResponseGetOrder> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseGetOrder>, response: Response<ResponseGetOrder>) {
                if (response.isSuccessful) {
                    response.body()?.data?.transactionOrder?.let { transactions ->
                        transaksiList.clear()
                        transaksiList.addAll(transactions)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("FetchTransaksiError", "Error: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Gagal mengambil data transaksi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseGetOrder>, t: Throwable) {
                Toast.makeText(requireContext(), "Kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}