package com.nafis.moneylaundry.transaction

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nafis.moneylaundry.SharedPreferencesHelper
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.api.ApiService
import com.nafis.moneylaundry.data.SelectImage
import com.nafis.moneylaundry.databinding.ActivityAddPaketBinding
import com.nafis.moneylaundry.extensions.toPaketLaundryModel
import com.nafis.moneylaundry.models.packageLaundry.PaketLaundryModel
import com.nafis.moneylaundry.models.packageLaundry.ResponseCreatePackage
import com.nafis.moneylaundry.repository.LaundryRepository
import com.nafis.moneylaundry.sheet.SelectImageBottomSheet
import com.nafis.moneylaundry.viewmodel.UserViewModel
import com.nafis.moneylaundry.viewmodel.UserViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPaketActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPaketBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var apiService: ApiService
    private var selectedImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPaketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = LaundryRepository(ApiClient, application)
        val factory = UserViewModelFactory(application, repository)
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
        apiService = ApiClient.instance

        binding.ivBackButton.setOnClickListener {
            @Suppress("DEPRECATION")
            onBackPressed()
        }

        binding.circleImageView.setOnClickListener {
            showSelectImageBottomSheet()
        }

        binding.btnBuatPaket.setOnClickListener {
            addPaketLaundry()
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

    private fun addPaketLaundry() {
        val name = binding.edtNamaPaket.text.toString().trim()
        Log.e("AddPaketActivity", "Nama paket: $name")
        val pricePerKg = binding.edtHarga.text.toString().trim().toIntOrNull()
        Log.e("AddPaketActivity", "Harga per kg: $pricePerKg")
        val description = binding.edtDeskripsiPaket.text.toString().trim()
        Log.e("AddPaketActivity", "Deskripsi: $description")

        if (name.isEmpty() || pricePerKg == null || pricePerKg <= 0 || description.isEmpty()) {
            Toast.makeText(this, "Harap lengkapi semua field.", Toast.LENGTH_SHORT).show()
            return
        }

        val paketLaundry = PaketLaundryModel(
            name = name,
            price_per_kg = pricePerKg,
            description = description,
            logo = selectedImage ?: ""
        )

        createPackageLaundry(paketLaundry)
    }

    private fun createPackageLaundry(paketLaundryModel: PaketLaundryModel) {
        val token = SharedPreferencesHelper(this).getToken()

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        if (!userViewModel.canAddPaketLaundry()) {
            Toast.makeText(this, "Akun Basic hanya bisa menambahkan maksimal 3 paket laundry.", Toast.LENGTH_SHORT).show()
            return
        }

        apiService.createPackageLaundry("Bearer $token", paketLaundryModel).enqueue(object : Callback<ResponseCreatePackage> {
            override fun onResponse(call: Call<ResponseCreatePackage>, response: Response<ResponseCreatePackage>) {
                if (response.isSuccessful) {
                    val newPaketLaundry = response.body()?.data
                    if (newPaketLaundry != null) {
                        userViewModel.addPaketLaundry(newPaketLaundry.toPaketLaundryModel()!!)

                        val packageLaundryId = newPaketLaundry.packageLaundryId ?: -1
                        userViewModel.savePackageLaundryId(packageLaundryId)

                        val sharedPreferencesHelper = SharedPreferencesHelper(this@AddPaketActivity)
                        sharedPreferencesHelper.savePackageName(newPaketLaundry.name ?: "Default Package Name")
                        sharedPreferencesHelper.savePackageDescription(newPaketLaundry.description ?: "Default Description")
                        sharedPreferencesHelper.savePricePerKg(newPaketLaundry.pricePerKg ?: 0)
                        sharedPreferencesHelper.saveLogoUrl(selectedImage ?: "")
                        Log.d("AddPaketActivity", "Saved logo URL: ${selectedImage ?: "No image selected"}")

                        val retrievedId = userViewModel.getPackageLaundryId()
                        Log.d("AddPaketActivity", "Saved package_laundry_id: $retrievedId")

                        userViewModel.fetchPaketLaundry()
                    }
                    Toast.makeText(this@AddPaketActivity, "Paket berhasil dibuat", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddPaketActivity, "Gagal menambahkan paket: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseCreatePackage>, t: Throwable) {
                Toast.makeText(this@AddPaketActivity, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}


