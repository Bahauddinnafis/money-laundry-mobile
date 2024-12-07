package com.nafis.moneylaundry.transaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.SharedPreferencesHelper
import com.nafis.moneylaundry.data.AddonDetail
import com.nafis.moneylaundry.databinding.ActivityOrderStatusBinding
import com.nafis.moneylaundry.models.packageLaundry.PaketLaundryModel

class OrderStatusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderStatusBinding

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        val paketLaundry = intent.getParcelableExtra<PaketLaundryModel>("paketLaundry")
        paketLaundry?.let {
            binding.edtNamaPaket.setText(it.name)
            binding.tvTransaksiBaru.text = it.name
            binding.edtDeskripsiPaket.setText(it.description)

            binding.edtNamaPaket.isFocusable = false
            binding.edtNamaPaket.isClickable = false
            binding.edtNamaPaket.isCursorVisible = false
        }

        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        binding.edtNamaPaket.setText(sharedPreferencesHelper.getPackageName())
        binding.edtDeskripsiPaket.setText(sharedPreferencesHelper.getPackageDescription())
        binding.edtCustomer.setText(sharedPreferencesHelper.getCustomerName())
        binding.edtNomorTelpon.setText(sharedPreferencesHelper.getCustomerPhoneNumber())
        binding.edtBerat.setText(sharedPreferencesHelper.getWeight().toString())
        binding.edtOrderDate.setText(sharedPreferencesHelper.getOrderDate())
        binding.edtPickUpDate.setText(sharedPreferencesHelper.getPickUpDate())
        binding.edtQty.setText(sharedPreferencesHelper.getQuantity().toString())

        val paymentStatus = sharedPreferencesHelper.getPaymentStatus()
        binding.radioGroup.check(if (paymentStatus == "paid") R.id.radioLunas else R.id.radioBelumBayar)

        val subtotal = sharedPreferencesHelper.getSubtotal()
        val subtotalAddOnItem = sharedPreferencesHelper.getSubtotalAddOnItem()
        val totalPrice = sharedPreferencesHelper.getTotalPrice()

        binding.tvTotalLaundry.text = "Harga Laundry Utama : Rp. ${String.format("%,.2f", subtotal.toDouble())}"
        binding.tvTotalAddons.text = "Harga Item Tambahan : Rp. ${String.format("%,.2f", subtotalAddOnItem.toDouble())}"
        binding.tvHargaTransaksi.text = "Total Transaksi : Rp. ${String.format("%,.2f", totalPrice.toDouble())}"

        val addons = sharedPreferencesHelper.getAddons()
        updateAddonsLayout(addons)

        binding.ivBackButton.setOnClickListener {
            onBackPressed()
        }
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
}