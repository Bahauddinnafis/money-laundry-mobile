package com.nafis.moneylaundry.transaction

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.SharedPreferencesHelper
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.data.AddonDetail
import com.nafis.moneylaundry.databinding.ActivityOrderStatusBinding
import com.nafis.moneylaundry.models.transactions.AddOnItemItemTransaction
import com.nafis.moneylaundry.models.transactions.DataTransaction
import com.nafis.moneylaundry.models.transactions.ResponseSendInvoice
import com.nafis.moneylaundry.models.transactions.ResponseTransactionOrder
import com.nafis.moneylaundry.models.transactions.ResponseUpdatePayment
import com.nafis.moneylaundry.sheet.AddonsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("NAME_SHADOWING")
class OrderStatusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderStatusBinding
    private lateinit var addonsAdapter: AddonsAdapter
    private val addonsList: MutableList<AddonDetail> = mutableListOf()

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Data Addons Recyclerview
        addonsAdapter = AddonsAdapter(addonsList, object : AddonsAdapter.OnAddonClickListener {
            override fun onAddonClick(addon: AddonDetail) {
                Toast.makeText(this@OrderStatusActivity, "Clicked: ${addon.addonName}", Toast.LENGTH_SHORT).show()
            }

            override fun onAddonLongClick(addon: AddonDetail) {
                Toast.makeText(this@OrderStatusActivity, "Long Clicked: ${addon.addonName}", Toast.LENGTH_SHORT).show()
            }
        })

        binding.recyclerViewAddons.adapter = addonsAdapter
        binding.recyclerViewAddons.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val transactionOrderId = intent.getIntExtra("transactionOrderId", -1)

        if (transactionOrderId != -1) {
            fetchTransactionDetails(transactionOrderId)
        } else {
            Toast.makeText(this, "Transaction ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Update Payment Status
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val sharedPreferencesHelper = SharedPreferencesHelper(this)
            val token = "Bearer ${sharedPreferencesHelper.getToken()}"

            val paymentStatus = when (checkedId) {
                R.id.radioLunas -> "paid"
                R.id.radioBelumBayar -> "unpaid"
                else -> null
            }

            if (paymentStatus != null) {
                val transactionOrderId = intent.getIntExtra("transactionOrderId", -1)
                if (transactionOrderId != -1) {
                    updatePaymentStatus(token, transactionOrderId, paymentStatus)
                } else {
                    Toast.makeText(this, "Transaction ID tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Send Invoice to WhatsApp
        binding.btnSendWa.setOnClickListener {
            val transactionOrderId = intent.getIntExtra("transactionOrderId", -1)

            if (transactionOrderId != -1) {
                sendInvoiceToWhatsApp(transactionOrderId)
            } else {
                Toast.makeText(this, "Transaction ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivBackButton.setOnClickListener {
            @Suppress("DEPRECATION")
            onBackPressed()
        }
    }

    private fun fetchTransactionDetails(transactionOrderId: Int) {
        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        val token = "Bearer ${sharedPreferencesHelper.getToken()}"

        ApiClient.instance.getTransactionOrder(token, transactionOrderId).enqueue(object :
            Callback<ResponseTransactionOrder> {
            override fun onResponse(call: Call<ResponseTransactionOrder>, response: Response<ResponseTransactionOrder>) {
                if (response.isSuccessful) {
                    val transactionDetails = response.body()?.data
                    updateUI(transactionDetails)
                } else {
                    Log.e("FetchTransactionError", "Error: ${response.errorBody()?.string()}")
                    Toast.makeText(this@OrderStatusActivity, "Gagal mengambil detail transaksi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseTransactionOrder>, t: Throwable) {
                Toast.makeText(this@OrderStatusActivity, "Kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updatePaymentStatus(token: String, transactionOrderId: Int, paymentStatus: String) {
        val requestBody = mapOf("payment_status" to paymentStatus)

        ApiClient.instance.updatePayment(token, transactionOrderId, requestBody).enqueue(object : Callback<ResponseUpdatePayment> {
            override fun onResponse(call: Call<ResponseUpdatePayment>, response: Response<ResponseUpdatePayment>) {
                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "Status pembayaran berhasil diperbarui"
                    Toast.makeText(this@OrderStatusActivity, message, Toast.LENGTH_SHORT).show()
                    Log.d("UpdatePayment", "Berhasil: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("UpdatePaymentError", "Error: $errorBody").toString()
                    Toast.makeText(this@OrderStatusActivity, "Gagal memperbarui status pembayaran: $errorBody", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseUpdatePayment>, t: Throwable) {
                Log.e("UpdatePaymentFailure", "Kesalahan: ${t.message}")
                Toast.makeText(this@OrderStatusActivity, "Kesalahan jaringan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendInvoiceToWhatsApp(transactionOrderId: Int) {
        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        val token = "Bearer ${sharedPreferencesHelper.getToken()}"

        val requestBody = mapOf("transaction_order_id" to transactionOrderId)

        ApiClient.instance.sendInvoiceWA(token, requestBody).enqueue(object : Callback<ResponseSendInvoice> {
            override fun onResponse(call: Call<ResponseSendInvoice>, response: Response<ResponseSendInvoice>) {
                if (response.isSuccessful) {
                    val waUrl = response.body()?.data
                    if (waUrl != null) {
                        openWhatsApp(waUrl)
                    } else {
                        Toast.makeText(this@OrderStatusActivity, "URL WhatsApp tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("SendInvoiceError", "Error: $errorBody")
                    Toast.makeText(this@OrderStatusActivity, "Gagal mengirim nota ke WhatsApp: $errorBody", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseSendInvoice>, t: Throwable) {
                Log.e("SendInvoiceFailure", "Kesalahan: ${t.message}")
                Toast.makeText(this@OrderStatusActivity, "Kesalahan jaringan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openWhatsApp(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.setPackage("com.whatsapp")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp tidak ditemukan di perangkat ini", Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("SetTextI18n", "DefaultLocale", "NotifyDataSetChanged")
    private fun updateUI(transactionDetails: DataTransaction?) {
        transactionDetails?.let { details ->
            binding.edtCustomer.setText(details.customer?.name)
            binding.edtNomorTelpon.setText(details.customer?.phoneNumber)
            binding.edtNamaPaket.setText(details.packageLaundry?.name)
            binding.edtDeskripsiPaket.setText(details.packageLaundry?.description)
            binding.edtBerat.setText(details.weight.toString())
            binding.edtOrderDate.setText(details.orderDate)
            binding.edtPickUpDate.setText(details.pickUpDate)
            binding.edtQty.setText(details.quantity.toString())

            val paymentStatus = details.paymentStatus
            binding.radioGroup.check(if (paymentStatus == "paid") R.id.radioLunas else R.id.radioBelumBayar)

            val subtotal = details.subtotal
            val subtotalAddOnItem = details.subtotalAddOnItem
            val totalPrice = details.totalPrice

            binding.tvTotalLaundry.text = "Harga Laundry Utama : Rp. ${String.format("%,.2f", subtotal?.toDouble() ?: 0.0)}"
            binding.tvTotalAddons.text = "Harga Item Tambahan : Rp. ${String.format("%,.2f", subtotalAddOnItem?.toDouble() ?: 0.0)}"
            binding.tvHargaTransaksi.text = "Total Transaksi : Rp. ${String.format("%,.2f", (totalPrice?.toDouble() ?: 0.0))}"

            val addonsFromApi = details.addOnItem?.map {
                AddonDetail(
                    addonName = it?.itemName ?: "Nama Item Tidak Ditemukan",
                    addonPrice = "${it?.pricePerItem ?: 0}",
                    addonQuantity = "${it?.quantity ?: 0}",
                    totalPrice = "${(it?.pricePerItem?.toDouble() ?: 0.0) * (it?.quantity ?: 0)}"
                )
            } ?: emptyList()

            addonsList.clear()
            addonsList.addAll(addonsFromApi)
            addonsAdapter.notifyDataSetChanged()

            updateAddonsLayout(details.addOnItem)
        }
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun updateAddonsLayout(addOnItem: List<AddOnItemItemTransaction?>?) {
        binding.llVerticalAddons.removeAllViews()
        addOnItem?.let { addons ->
            for (addon in addons) {
                val addonPrice = addon?.pricePerItem?.toDouble() ?: 0.0
                val addonQuantity = addon?.quantity ?: 0
                val totalPrice = addonPrice * addonQuantity

                val addonLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.END
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 8, 16, 0)
                    }
                }

                val tvAddonName = TextView(this).apply {
                    text = addon?.itemName ?: "Nama Item Tidak Ditemukan"
                    textSize = 14f
                }

                val tvAddonQuantity = TextView(this).apply {
                    text = " x $addonQuantity : "
                    textSize = 14f
                }

                val tvAddonPrice = TextView(this).apply {
                    text = "Rp. ${String.format("%,.2f", totalPrice)}"
                    textSize = 14f
                }

                addonLayout.addView(tvAddonName)
                addonLayout.addView(tvAddonQuantity)
                addonLayout.addView(tvAddonPrice)

                binding.llVerticalAddons.addView(addonLayout)
            }
        }
    }
}