package com.nafis.moneylaundry.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.nafis.moneylaundry.SharedPreferencesHelper
import com.nafis.moneylaundry.adapter.DaftarTransaksiRecyclerview
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.databinding.FragmentDaftarTransaksiBinding
import com.nafis.moneylaundry.models.transactions.ResponseGetOrder
import com.nafis.moneylaundry.models.transactions.ResponseTransactionOrder
import com.nafis.moneylaundry.models.transactions.TransactionOrderItemFilter
import com.nafis.moneylaundry.transaction.OrderStatusActivity
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.models.transactions.ResponseFilterOrder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DaftarTransaksiFragment : Fragment() {
    private var _binding: FragmentDaftarTransaksiBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DaftarTransaksiRecyclerview
    private val transaksiList = mutableListOf<TransactionOrderItemFilter?>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDaftarTransaksiBinding.inflate(inflater, container, false)

        @Suppress("DEPRECATION")
        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i) as Chip
                chip.isChecked = chip.id == checkedId
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardFilterLanjutan.setOnClickListener {
            showFilterBottomSheet()
        }

        setUpRecyclerView()
        fetchTransactionData()

        @Suppress("DEPRECATION")
        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            val userId = SharedPreferencesHelper(requireContext()).getUserId()
            val paymentStatus: String? = when (checkedId) {
                R.id.chipLunas -> "paid"
                R.id.chipBelumLunas -> "unpaid"
                else -> null
            }

            val orderStatus: String? = when (checkedId) {
                R.id.chipSelesai -> "done"
                R.id.chipProses -> "on process"
                else -> null
            }

            filterTransaction(userId, null, null, paymentStatus, orderStatus)
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

    @SuppressLint("InflateParams")
    private fun showFilterBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_filter, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val chipGroup = bottomSheetView.findViewById<ChipGroup>(R.id.chipGroupBottomSheet)
        val etTanggalMulai = bottomSheetView.findViewById<EditText>(R.id.etTanggalMulai)
        val etTanggalSelesai = bottomSheetView.findViewById<EditText>(R.id.etTanggalSelesai)
        val btnApplyFilter = bottomSheetView.findViewById<Button>(R.id.btnApplyFilter)

        btnApplyFilter.setOnClickListener {
            val userId = SharedPreferencesHelper(requireContext()).getUserId()
            val startDate = etTanggalMulai.text.toString().takeIf { it.isNotEmpty() }
            val endDate = etTanggalSelesai.text.toString().takeIf { it.isNotEmpty() }

            val selectedChipId = chipGroup.checkedChipId
            val paymentStatus = when (selectedChipId) {
                R.id.chipLunasBottom -> "paid"
                R.id.chipBelumLunasBottom -> "unpaid"
                else -> null
            }

            val orderStatus = when (selectedChipId) {
                R.id.chipSelesaiBottom -> "done"
                R.id.chipProsesBottom -> "on process"
                else -> null
            }

            filterTransaction(userId, startDate, endDate, paymentStatus, orderStatus)
            bottomSheetDialog.dismiss()
        }

        etTanggalMulai.setOnClickListener {
            showDatePickerDialog(etTanggalMulai)
        }

        etTanggalSelesai.setOnClickListener {
            showDatePickerDialog(etTanggalSelesai)
        }

        bottomSheetDialog.show()
    }

    private fun filterTransaction(userId: Int, startDate: String?, endDate: String?, paymentStatus: String?, orderStatus: String?) {
        val sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val token = "Bearer ${sharedPreferencesHelper.getToken()}"

        val requestBody = mutableMapOf<String, String>()
        startDate?.let { requestBody["start_date"] = it }
        endDate?.let { requestBody["end_date"] = it }
        paymentStatus?.let { requestBody["payment_status"] = it }
        orderStatus?.let { requestBody["status"] = it }

        Log.d("FilterTransaction", "Request Body: $requestBody")

        ApiClient.instance.filterTransactionOrder(token, userId, requestBody).enqueue(object : Callback<ResponseFilterOrder> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseFilterOrder>, response: Response<ResponseFilterOrder>) {
                if (response.isSuccessful) {
                    response.body()?.data?.transactionOrder?.let { transactions ->
                        Log.d("FilterTransaction", "Transactions: $transactions") // Log transactions
                        transaksiList.clear()
                        transaksiList.addAll(transactions)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("FilterTransactionError", "Error: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Gagal memfilter transaksi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseFilterOrder>, t: Throwable) {
                Toast.makeText(requireContext(), "Kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("DefaultLocale")
    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            editText.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }
}