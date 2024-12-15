package com.nafis.moneylaundry.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.nafis.moneylaundry.SharedPreferencesHelper
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.databinding.FragmentMoneyBinding
import com.nafis.moneylaundry.models.money.ResponseMoney
import com.nafis.moneylaundry.models.money.TotalMoneyItem
import com.nafis.moneylaundry.models.money.TotalTransactionItem
import com.nafis.moneylaundry.transaction.PremiumActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class MoneyFragment : Fragment() {
    private var _binding: FragmentMoneyBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoneyBinding.inflate(inflater, container, false)

        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val accountStatusId = sharedPreferencesHelper.getAccountStatus()
        Log.d("HomeFragment", "Account Status ID: $accountStatusId")
        getMoneyData()

        if (accountStatusId == 1) {
            showUpgradeMessage()
        }

        binding.btnPremium.setOnClickListener {
            val intent = Intent(requireContext(), PremiumActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun showUpgradeMessage() {
        binding.viewBlur.visibility = View.VISIBLE
        binding.tvUpgradeMessage.visibility = View.VISIBLE
        binding.btnPremium.visibility = View.VISIBLE
    }

    private fun getMoneyData() {
        val userId = sharedPreferencesHelper.getUserId()
        val token = sharedPreferencesHelper.getToken()

        if (userId != -1 && token != null) {
            ApiClient.instance.getMoneyData("Bearer $token", userId).enqueue(object :
                Callback<ResponseMoney> {
                override fun onResponse(call: Call<ResponseMoney>, response: Response<ResponseMoney>) {
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!.data

                        val responseJson = Gson().toJson(response.body())
                        Log.d("API Response JSON", responseJson)
                        Log.d("API Response", "Data received: ${data?.totalMoney}")

                        data?.let {
                            binding.tvJumlahPesananMasuk.text = it.transactionOrderNew?.toString() ?: "0"
                            binding.tvJumlahPesananProses.text = it.transactionOrderOnProcess?.toString() ?: "0"
                            binding.tvJumlahPesananSelesai.text = it.transactionOrderDone?.toString() ?: "0"
                            val pendapatan = it.transactionOrderIncome?.toDoubleOrNull() ?: 0.0
                            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                            binding.tvJumlahPendapatan.text = formatRupiah.format(pendapatan)
                            Log.d("Transaction Data", "New Orders: ${it.transactionOrderNew}")
                            Log.d("Transaction Data", "Orders On Process: ${it.transactionOrderOnProcess}")
                            Log.d("Transaction Data", "Orders Done: ${it.transactionOrderDone}")
                            Log.d("Transaction Data", "Income: ${it.transactionOrderIncome}")

                            it.totalMoney?.forEachIndexed { index, item ->
                                Log.d("MoneyData", "Index: $index, Day: ${item?.orderDay}, TotalMoney: ${item?.totalMoney}")
                            }
                            it.totalTransaction?.forEachIndexed { index, item ->
                                Log.d("TransactionData", "Index: $index, Day: ${item?.orderDay}, TotalTransactions: ${item?.totalTransaction}, OrderDate: ${item?.orderDate}")
                            }

                            setupBarChartPendapatan(it.totalMoney)
                            setupBarChartTransaksi(it.totalTransaction)
                        }
                    } else {
                        Log.e("MoneyFragment", "Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseMoney>, t: Throwable) {
                    Log.e("MoneyFragment", "Failure: ${t.message}")
                }
            })
        }
    }

    private fun setupBarChartPendapatan(totalMoney: List<TotalMoneyItem?>?) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        val dayAbbreviations = mapOf(
            "Monday" to "Sen",
            "Tuesday" to "Sel",
            "Wednesday" to "Rab",
            "Thursday" to "Kam",
            "Friday" to "Jum",
            "Saturday" to "Sab",
            "Sunday" to "Min"
        )

        binding.chartPendapatan.clear()

        totalMoney?.forEachIndexed { index, item ->
            Log.d("TotalMoneyItem", "Index: $index, Total: ${item?.totalMoney}, Day: ${item?.orderDay}")
            item?.totalMoney?.let {
                entries.add(BarEntry(index.toFloat(), it.toFloat()))
                labels.add(dayAbbreviations[item.orderDay] ?: item.orderDay ?: "")
            }
        }

        val barDataSet = BarDataSet(entries, "Pendapatan")
        barDataSet.color = Color.rgb(0, 0, 102)
        val barData = BarData(barDataSet)
        binding.chartPendapatan.data = barData

        binding.chartPendapatan.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                return labels.getOrNull(value.toInt()) ?: ""
            }
        }

        binding.chartPendapatan.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.chartPendapatan.axisRight.setDrawGridLines(false)
        binding.chartPendapatan.axisRight.setDrawLabels(false)
        binding.chartPendapatan.axisLeft.setDrawLabels(true)
        binding.chartPendapatan.axisLeft.axisMinimum = 0f
        binding.chartPendapatan.invalidate()

        entries.forEachIndexed { index, entry ->
            Log.d("BarEntryPendapatan", "Index: $index, Value: ${entry.y}")
        }
        labels.forEachIndexed { index, label ->
            Log.d("BarLabelPendapatan", "Index: $index, Label: $label")
        }
    }

    private fun setupBarChartTransaksi(totalTransaction: List<TotalTransactionItem?>?) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        val dayAbbreviations = mapOf(
            "Monday" to "Sen",
            "Tuesday" to "Sel",
            "Wednesday" to "Rab",
            "Thursday" to "Kam",
            "Friday" to "Jum",
            "Saturday" to "Sab",
            "Sunday" to "Min"
        )

        totalTransaction?.forEachIndexed { index, item ->
            item?.totalTransaction?.let {
                entries.add(BarEntry(index.toFloat(), it.toFloat()))
                labels.add(dayAbbreviations[item.orderDay] ?: item.orderDay ?: "")
            }
        }

        val barDataSet = BarDataSet(entries, "Transaksi")
        barDataSet.color = Color.rgb(0, 0, 102)
        val barData = BarData(barDataSet)
        binding.chartTransaksi.data = barData

        binding.chartTransaksi.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                return labels.getOrNull(value.toInt()) ?: ""
            }
        }

        binding.chartTransaksi.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.chartTransaksi.axisRight.setDrawLabels(false)
        binding.chartTransaksi.axisRight.setDrawGridLines(false)
        binding.chartTransaksi.axisLeft.setDrawLabels(true)
        binding.chartTransaksi.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}