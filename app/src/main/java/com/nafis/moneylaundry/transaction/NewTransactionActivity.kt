package com.nafis.moneylaundry.transaction

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nafis.moneylaundry.SharedPreferencesHelper
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.data.AddonDetail
import com.nafis.moneylaundry.databinding.ActivityNewTransactionBinding
import com.nafis.moneylaundry.sheet.AddOnsBottomSheetFragment
import com.nafis.moneylaundry.models.packageLaundry.PaketLaundryModel
import com.nafis.moneylaundry.models.transactions.AddOnItem
import com.nafis.moneylaundry.models.transactions.CreateOrderRequest
import com.nafis.moneylaundry.models.transactions.ResponseCreateOrder
import com.nafis.moneylaundry.sheet.AddonsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewTransactionActivity : AppCompatActivity(), OnAddonsAddedListener, AddonsAdapter.OnAddonClickListener {
    private lateinit var binding: ActivityNewTransactionBinding
    private val addonsList = mutableListOf<AddonDetail>()
    private lateinit var addonsAdapter: AddonsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateTotalAddonsPrice()
        updateLaundryPriceDisplay()
        calculateTotalTransaction()
        toggleTotalTransactionVisibility()

        addonsAdapter = AddonsAdapter(addonsList, this)
        binding.recyclerViewAddons.adapter = addonsAdapter
        binding.recyclerViewAddons.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        @Suppress("DEPRECATION") val paketLaundry  = intent.getParcelableExtra<PaketLaundryModel>("paketLaundry")
        paketLaundry?.let {
            Log.d("NewTransactionActivity", "Received package laundry ID: ${it.package_laundry_id}")
            binding.edtNamaPaket.setText(it.name)
            binding.tvTransaksiBaru.text = it.name
            binding.edtDeskripsiPaket.setText(it.description)

            binding.edtNamaPaket.isFocusable = false
            binding.edtNamaPaket.isClickable = false
            binding.edtNamaPaket.isCursorVisible = false

        }

        binding.tilAddOns.setOnClickListener {
            showAddonsBottomSheet()
        }

        binding.edtBerat.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateLaundryPriceDisplay()
                calculateTotalTransaction()
                toggleTotalTransactionVisibility()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.edtAddOns.setOnClickListener {
            showAddonsBottomSheet()
        }
        binding.edtOrderDate.setOnClickListener {
            showDatePickerDialog(binding.edtOrderDate)
        }
        binding.edtPickUpDate.setOnClickListener {
            showDatePickerDialog(binding.edtPickUpDate)
        }
        binding.ivBackButton.setOnClickListener {
            finish()
        }
        binding.btnBuatPesanan.setOnClickListener {
            createOrder(paketLaundry)
        }
    }

    private fun showAddonsBottomSheet() {
        val bottomSheetFragment = AddOnsBottomSheetFragment()
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onAddonsAdded(addonName: String, addonPrice: String, addonQuantity: String, totalPrice: String) {
        val newAddon = AddonDetail(addonName, addonPrice, addonQuantity, totalPrice)
        addonsList.add(newAddon)
        addonsAdapter.notifyDataSetChanged()
        updateAddonsLayout(addonsList)
        updateTotalAddonsPrice()
        calculateTotalTransaction()
        toggleTotalTransactionVisibility()
    }


    override fun onAddonUpdated(
        addon: AddonDetail,
        addonName: String,
        addonPrice: String,
        addonQuantity: String,
        totalPrice: String
    ) {
        val existingAddonIndex = addonsList.indexOfFirst { it.addonName == addon.addonName }
        if (existingAddonIndex != -1) {
            val updatedAddon = AddonDetail(addonName, addonPrice, addonQuantity, totalPrice)
            addonsList[existingAddonIndex] = updatedAddon
            addonsAdapter.notifyItemChanged(existingAddonIndex)

            updateAddonsLayout(addonsList)
            updateTotalAddonsPrice()
            calculateTotalTransaction()
        }
    }


    override fun onAddonClick(addon: AddonDetail) {
        editAddon(addon)
    }

    override fun onAddonLongClick(addon: AddonDetail) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Addon")
            .setMessage("Apakah Anda yakin ingin menghapus addon ini?")
            .setPositiveButton("Ya") { _, _ ->
                deleteAddon(addon)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun updateAddonsLayout(addons: List<AddonDetail>) {
        binding.llVerticalAddons.removeAllViews()
        for (addon in addons) {
            val addonPrice = addon.addonPrice.toDoubleOrNull() ?: 0.0
            val addonQuantity = addon.addonQuantity.toIntOrNull() ?: 0
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
                text = addon.addonName
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val tvAddonQuantity = TextView(this).apply {
                text = " x ${addon.addonQuantity} : "
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val tvAddonPrice = TextView(this).apply {
                text = "Rp. ${String.format("%,.2f", totalPrice)}"
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            addonLayout.addView(tvAddonName)
            addonLayout.addView(tvAddonQuantity)
            addonLayout.addView(tvAddonPrice)

            binding.llVerticalAddons.addView(addonLayout)
        }
    }

    private fun calculateTotalAddonsPrice(): Double {
        var totalPrice = 0.0
        if (addonsList.isNotEmpty()) {
            for (addon in addonsList) {
                val addonPrice = addon.addonPrice.toDoubleOrNull() ?: 0.0
                val addonQuantity = addon.addonQuantity.toIntOrNull() ?: 0
                totalPrice += addonPrice * addonQuantity
            }
        }
        return totalPrice
    }

    @SuppressLint("DefaultLocale")
    private fun updateTotalAddonsPrice() {
        val totalPrice = calculateTotalAddonsPrice()
        binding.tvHargaAddons.text = if (totalPrice > 0) {
            "Rp. ${String.format("%,.2f", totalPrice)}"
        } else {
            "Rp. 0"
        }
    }

    private fun getTotalLaundryPrice(): Int {
        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        val pricePerKg = sharedPreferencesHelper.getPricePerKg()

        val weightInput = binding.edtBerat.text.toString().trim()
        val weight = weightInput.toIntOrNull() ?: 0

        return pricePerKg * weight
    }

    @SuppressLint("DefaultLocale")
    private fun updateLaundryPriceDisplay() {
        val totalPrice = getTotalLaundryPrice()

        binding.tvHargaLaundry.text = if (totalPrice > 0) {
            "Rp. ${String.format("%,.2f", totalPrice.toDouble())}"
        } else {
            "Rp. 0"
        }
    }

    @SuppressLint("DefaultLocale")
    private fun calculateTotalTransaction() {
        val totalAddons = calculateTotalAddonsPrice()

        val totalLaundry = getTotalLaundryPrice()

        val totalTransaction = totalAddons + totalLaundry

        binding.tvHargaTransaksi.text = if (totalTransaction > 0) {
            "Rp. ${String.format("%,.2f", totalTransaction)}"
        } else {
            "Rp. 0"
        }
    }

    private fun toggleTotalTransactionVisibility() {
        val totalAddons = calculateTotalAddonsPrice()
        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        val weightInput = binding.edtBerat.text.toString().trim()
        val weight = weightInput.toIntOrNull() ?: 0
        val totalLaundry = sharedPreferencesHelper.getPricePerKg() * weight

        val totalTransaction = totalAddons + totalLaundry
        binding.llTotalTransaksi.visibility = if (totalTransaction > 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun editAddon(existingAddon: AddonDetail) {
        val bottomSheetFragment = AddOnsBottomSheetFragment()
        val bundle = Bundle()
        bundle.putParcelable("existingAddon", existingAddon)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)

        updateTotalAddonsPrice()
        calculateTotalTransaction()
        toggleTotalTransactionVisibility()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun deleteAddon(addon: AddonDetail) {
        addonsList.remove(addon)
        addonsAdapter.notifyDataSetChanged()
        updateAddonsLayout(addonsList)
        updateTotalAddonsPrice()
        calculateTotalTransaction()
        toggleTotalTransactionVisibility()
    }

    @SuppressLint("DefaultLocale")
    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                editText.setText(date)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun createOrder(paketLaundry: PaketLaundryModel?) {
        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        val userId = sharedPreferencesHelper.getUserId()
        val packageLaundryId = paketLaundry?.package_laundry_id ?: -1
        val logoUrl = sharedPreferencesHelper.getLogoUrl()
        Log.d("NewTransactionActivity", "Retrieved logo URL: $logoUrl")

        val customerName = binding.edtCustomer.text.toString()
        val customerPhoneNumber = binding.edtNomorTelpon.text.toString()
        val orderDate = binding.edtOrderDate.text.toString()
        val pickUpDate = binding.edtPickUpDate.text.toString()
        val weight = binding.edtBerat.text.toString().toIntOrNull() ?: 0
        val subtotal = getTotalLaundryPrice()
        val subtotalAddOnItem = calculateTotalAddonsPrice().toInt()
        val totalPrice = subtotal + subtotalAddOnItem
        val quantity = binding.edtQty.text.toString().toIntOrNull() ?: 0
        val paymentStatus = if (binding.radioLunas.isChecked) "paid" else "unpaid"

        sharedPreferencesHelper.saveCustomerName(customerName)
        sharedPreferencesHelper.saveCustomerPhoneNumber(customerPhoneNumber)
        sharedPreferencesHelper.savePackageName(binding.edtNamaPaket.text.toString())
        sharedPreferencesHelper.savePackageDescription(binding.edtDeskripsiPaket.text.toString())
        sharedPreferencesHelper.saveWeight(weight)
        sharedPreferencesHelper.saveOrderDate(orderDate)
        sharedPreferencesHelper.savePickUpDate(pickUpDate)
        sharedPreferencesHelper.saveTotalPrice(totalPrice)
        sharedPreferencesHelper.saveSubtotal(subtotal)
        sharedPreferencesHelper.saveSubtotalAddOnItem(subtotalAddOnItem)
        sharedPreferencesHelper.saveQuantity(quantity)
        sharedPreferencesHelper.savePaymentStatus(paymentStatus)
        sharedPreferencesHelper.saveAddons(addonsList)

        val addOnItems = addonsList.map { addon ->
            AddOnItem(
                itemName = addon.addonName,
                pricePerItem = addon.addonPrice.toInt(),
                quantity = addon.addonQuantity.toInt(),
                subtotal = addon.addonPrice.toInt() * addon.addonQuantity.toInt()
            )
        }

        val createOrderRequest = CreateOrderRequest(
            customerName = customerName,
            customerPhoneNumber = customerPhoneNumber,
            usersId = userId,
            packageLaundryId = packageLaundryId,
            orderDate = orderDate,
            pickUpDate = pickUpDate,
            status = "new",
            paymentStatus = paymentStatus,
            weight = weight,
            subtotal = subtotal,
            subtotalAddOnItem = subtotalAddOnItem,
            totalPrice = totalPrice,
            quantity = quantity,
            addOnItem = addOnItems,
        )

        val token = "Bearer ${sharedPreferencesHelper.getToken()}"
        ApiClient.instance.createOrder("Bearer $token", createOrderRequest).enqueue(object : Callback<ResponseCreateOrder> {
            override fun onResponse(call: Call<ResponseCreateOrder>, response: Response<ResponseCreateOrder>) {
                if (response.isSuccessful) {
                    val transactionOrderId = response.body()?.data?.transactionOrderId
                    transactionOrderId?.let { id ->
                        SharedPreferencesHelper(this@NewTransactionActivity)
                        sharedPreferencesHelper.saveTransactionOrderId(id)
                        Log.d("NewTransactionActivity", "Transaction Order ID saved: $id")
                    }
                    val intent = Intent(this@NewTransactionActivity, OrderStatusActivity::class.java)
                    intent.putExtra("transactionOrderId", transactionOrderId)
                    startActivity(intent)
                    finish()
                    Log.d("NewTransactionActivity", "Transaction Order ID: $transactionOrderId")
                } else {
                    Log.e("CreateOrderError", "Error: ${response.errorBody()?.string()}")
                    Toast.makeText(this@NewTransactionActivity, "Gagal mengirim data: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseCreateOrder>, t: Throwable) {
                Toast.makeText(this@NewTransactionActivity, "Kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}