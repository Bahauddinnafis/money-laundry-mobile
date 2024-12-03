package com.nafis.moneylaundry.transaction

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.data.SelectImage
import com.nafis.moneylaundry.databinding.ActivityEditPaketBinding
import com.nafis.moneylaundry.models.PaketLaundryModel
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

        // Inisialisasi ViewModel
        val laundryRepository = LaundryRepository(ApiClient, application)
        val factory = UserViewModelFactory(application, laundryRepository)
        userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        // Ambil data dari intent
        val paketLaundry = intent.getParcelableExtra<PaketLaundryModel>("paketLaundry")
        if (paketLaundry != null) {
            Log.e("EditPaketActivity", "Received paketLaundry: $paketLaundry")
            populateFormWithData(paketLaundry)
        } else {
            Toast.makeText(this, "Data paket tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Ambil ID paket laundry dari intent
        packageLaundryId = intent.getIntExtra("packageLaundryId", 0)
        if (packageLaundryId == 0) {
            Log.e("EditPaketActivity", "Received packageLaundryId is 0, cannot proceed.")
            Log.e("EditPaketActivity", "packageLaundryId: $packageLaundryId")
            Toast.makeText(this, "ID paket laundry tidak valid.", Toast.LENGTH_SHORT).show()
            return
        }

        // Observe hasil update paket laundry
        userViewModel.updatePaketResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Paket Laundry Berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish() // Kembali ke activity sebelumnya setelah berhasil
            }.onFailure { exception ->
                Toast.makeText(this, "Gagal memperbarui paket laundry: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol kembali
        binding.ivBackButton.setOnClickListener {
            finish()
        }

        // Tombol untuk memilih gambar
        binding.circleImageView.setOnClickListener {
            showSelectImageBottomSheet()
        }

        // Tombol untuk memperbarui paket laundry
        binding.btnBuatPaket.setOnClickListener {
            // Ambil data dari form
            val name = binding.edtNamaPaket.text.toString().trim()
            val pricePerKg = binding.edtHarga.text.toString().toIntOrNull()
            val description = binding.edtDeskripsiPaket.text.toString().trim()
            val logo = selectedImage ?: paketLaundry.logo

            if (name.isEmpty() || pricePerKg == null || pricePerKg <= 0 || description.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua field dengan benar.", Toast.LENGTH_SHORT).show()
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

    // Mengisi form dengan data paket laundry
    private fun populateFormWithData(paket: PaketLaundryModel) {
        binding.edtNamaPaket.setText(paket.name)
        Log.e("EditPaketActivity", "Populating form with data: $paket")
        binding.edtHarga.setText(paket.price_per_kg.toString())
        Log.e("EditPaketActivity", "Harga: ${paket.price_per_kg}")
        binding.edtDeskripsiPaket.setText(paket.description)
        Log.e("EditPaketActivity", "Deskripsi: ${paket.description}")

        // Menampilkan logo jika ada
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

    // Memperbarui paket laundry
    private fun updatePaketLaundry(paketLaundry: PaketLaundryModel) {
        val name = binding.edtNamaPaket.text.toString().trim()
        val pricePerKgString = binding.edtHarga.text.toString().trim()
        val pricePerKg = pricePerKgString.toIntOrNull()
        val description = binding.edtDeskripsiPaket.text.toString().trim()

        if (name.isEmpty() || pricePerKg == null || pricePerKg <= 0 || description.isEmpty()) {
            Toast.makeText(this, "Harap lengkapi semua field dengan benar.", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedPaketLaundry = paketLaundry.copy(
            name = name,
            price_per_kg = pricePerKg,
            description = description,
            logo = selectedImage ?: paketLaundry.logo // Gunakan logo yang ada jika tidak ada yang dipilih
        )

        userViewModel.updatePaketLaundry(updatedPaketLaundry) // Panggil fungsi update
    }

    // Menampilkan bottom sheet untuk memilih gambar
    private fun showSelectImageBottomSheet() {
        val bottomSheet = SelectImageBottomSheet { selectedImageName ->
            selectedImage = selectedImageName
            val selectedImageResId = SelectImage.allImage.find { it.imageName == selectedImageName }?.photo
            selectedImageResId?.let { binding.circleImageView.setImageResource(it) }
        }
        bottomSheet.show(supportFragmentManager, "SelectImageBottomSheet")
    }

    // Mendapatkan resource gambar berdasarkan nama
    private fun getImageResourceByName(context: Context, logoName: String): Int {
        return context.resources.getIdentifier(logoName, "drawable", context.packageName)
    }
}