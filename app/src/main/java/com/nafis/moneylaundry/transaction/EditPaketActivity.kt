package com.nafis.moneylaundry.transaction

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.data.SelectImage
import com.nafis.moneylaundry.databinding.ActivityEditPaketBinding
import com.nafis.moneylaundry.models.packageLaundry.PaketLaundryModel
import com.nafis.moneylaundry.repository.LaundryRepository
import com.nafis.moneylaundry.sheet.SelectImageBottomSheet
import com.nafis.moneylaundry.viewmodel.UserViewModel
import com.nafis.moneylaundry.viewmodel.UserViewModelFactory

class EditPaketActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPaketBinding
    private lateinit var userViewModel: UserViewModel
    private var selectedImage: String? = null
    private var packageLaundryId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPaketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val laundryRepository = LaundryRepository(ApiClient, application)
        val factory = UserViewModelFactory(application, laundryRepository)
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        @Suppress("DEPRECATION") val paketLaundry = intent.getParcelableExtra<PaketLaundryModel>("paketLaundry")
        if (paketLaundry != null) {
            Log.e("EditPaketActivity", "Received paketLaundry: $paketLaundry")
            populateFormWithData(paketLaundry)
        } else {
            Toast.makeText(this, "Data paket tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        packageLaundryId = intent.getIntExtra("packageLaundryId", 0)
        if (packageLaundryId == 0) {
            Log.e("EditPaketActivity", "Received packageLaundryId is 0, cannot proceed.")
            Log.e("EditPaketActivity", "packageLaundryId: $packageLaundryId")
            Toast.makeText(this, "ID paket laundry tidak valid.", Toast.LENGTH_SHORT).show()
            return
        }

        userViewModel.updatePaketResult.observe(this) { result ->
            result.onSuccess {
                showCustomToastSuccess(this, "Paket Laundry Berhasil diperbarui")
                finish()
            }.onFailure { exception ->
                showCustomToastError(this, "Gagal memperbarui paket laundry")
            }
        }

        binding.ivBackButton.setOnClickListener {
            finish()
        }

        binding.circleImageView.setOnClickListener {
            showSelectImageBottomSheet()
        }

        binding.btnBuatPaket.setOnClickListener {
            val name = binding.edtNamaPaket.text.toString().trim()
            val pricePerKg = binding.edtHarga.text.toString().toIntOrNull()
            val description = binding.edtDeskripsiPaket.text.toString().trim()
            val logo = selectedImage ?: paketLaundry.logo

            if (name.isEmpty() || pricePerKg == null || pricePerKg <= 0 || description.isEmpty()) {
                showCustomToastError(this, "Harap lengkapi semua field dengan benar.")
                return@setOnClickListener
            }

            val updatedPaketLaundry = paketLaundry.copy(
                name = name,
                price_per_kg = pricePerKg,
                description = description,
                logo = logo
            )

            userViewModel.updatePaketLaundry(updatedPaketLaundry)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populateFormWithData(paket: PaketLaundryModel) {
        binding.edtNamaPaket.setText(paket.name)
        Log.e("EditPaketActivity", "Populating form with data: $paket")
        binding.edtHarga.setText(paket.price_per_kg.toString())
        Log.e("EditPaketActivity", "Harga: ${paket.price_per_kg}")
        binding.edtDeskripsiPaket.setText(paket.description)
        Log.e("EditPaketActivity", "Deskripsi: ${paket.description}")

        if (paket.logo.isNotEmpty()) {
            val imageResource = getImageResourceByName(this, paket.logo)
            if (imageResource != 0) {
                Glide.with(this)
                    .load(imageResource)
                    .into(binding.circleImageView)
            } else {
                Log.e("EditPaketActivity", "Resource not found for logo: ${paket.logo}")
                binding.circleImageView.setImageResource(R.drawable.gambar1)
            }
        }
    }

    private fun showSelectImageBottomSheet() {
        val bottomSheet = SelectImageBottomSheet { selectedImageName ->
            selectedImage = selectedImageName
            val selectedImageResId = SelectImage.allImage.find { it.imageName == selectedImageName }?.photo
            selectedImageResId?.let { binding.circleImageView.setImageResource(it) }
        }
        bottomSheet.show(supportFragmentManager, "SelectImageBottomSheet")
    }

    @SuppressLint("DiscouragedApi")
    private fun getImageResourceByName(context: Context, logoName: String): Int {
        return context.resources.getIdentifier(logoName, "drawable", context.packageName)
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InflateParams")
    fun showCustomToastSuccess(activity: AppCompatActivity, message: String) {
        val inflater = activity.layoutInflater
        val layout: View = inflater.inflate(R.layout.custom_toast_success, null)

        val icon = layout.findViewById<ImageView>(R.id.toast_icon)
        val text = layout.findViewById<TextView>(R.id.toast_message)
        text.text = message
        icon.setImageResource(R.drawable.ic_check_circle)

        val toast = Toast(activity.applicationContext)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InflateParams")
    fun showCustomToastError(activity: AppCompatActivity, message: String) {
        val inflater = activity.layoutInflater
        val layout: View = inflater.inflate(R.layout.custom_toast_error, null)

        val icon = layout.findViewById<ImageView>(R.id.toast_icon)
        val text = layout.findViewById<TextView>(R.id.toast_message)
        text.text = message
        icon.setImageResource(R.drawable.ic_cancel)

        val toast = Toast(activity.applicationContext)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()
    }
}