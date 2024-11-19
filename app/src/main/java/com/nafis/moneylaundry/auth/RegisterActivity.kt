package com.nafis.moneylaundry.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.databinding.ActivityRegisterBinding
import com.nafis.moneylaundry.models.RegisterRequest
import com.nafis.moneylaundry.models.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val apiService = ApiClient.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.edtUsername.text.toString()
            val email = binding.edtEmail.text.toString()
            val storeName = binding.edtNamaToko.text.toString()
            val storeAddress = binding.edtAlamat.text.toString()
            val password = binding.edtPassword.text.toString()
            val passwordConfirmation = binding.edtPasswordConfirmation.text.toString()
            val phoneNumber = binding.edtNomorTelpon.text.toString()

            // Membuat objek RegisterRequest untuk dikirim ke API
            val registerRequest = RegisterRequest(
                name, email, storeName, storeAddress, password, passwordConfirmation, phoneNumber
            )

            // Mengonversi RegisterRequest menjadi Map<String, String>
            val registerRequestMap = registerRequest.toMap()

            // Memanggil API untuk registrasi
            apiService.registerUser(registerRequestMap).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        // Memperbaiki pengecekan status
                        if (registerResponse?.status == "true") {  // Jika status adalah String "true"
                            Log.d("RegisterActivity", "Registration successful: ${registerResponse.message}")
                            Toast.makeText(this@RegisterActivity, registerResponse.message, Toast.LENGTH_SHORT).show()

                            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("storeName", registerResponse.data?.store_name)
                            editor.putString("storeAddress", registerResponse.data?.store_address)
                            editor.apply()
                            // Debugging
                            Log.d("RegisterActivity", "Saved Store Name: ${registerResponse.data?.store_name}")
                            Log.d("RegisterActivity", "Saved Store Address: ${registerResponse.data?.store_address}")


                            // Navigasi ke LoginActivity setelah berhasil registrasi
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("RegisterActivity", "Registration failed: ${registerResponse?.message}")
                            Toast.makeText(this@RegisterActivity, "Registration failed: ${registerResponse?.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("RegisterActivity", "Registration failed: ${response.errorBody()?.string()}")
                        Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Log.e("RegisterActivity", "Error: ${t.message}")
                    Toast.makeText(this@RegisterActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.tvMasuk.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
