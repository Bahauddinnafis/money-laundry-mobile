package com.nafis.moneylaundry.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nafis.moneylaundry.MainActivity
import com.nafis.moneylaundry.SharedPreferencesHelper
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.databinding.ActivityLoginBinding
import com.nafis.moneylaundry.repository.LaundryRepository
import com.nafis.moneylaundry.viewmodel.UserViewModel
import com.nafis.moneylaundry.viewmodel.UserViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferencesHelper = SharedPreferencesHelper(this)

        // Periksa apakah token sudah ada, jika ya, arahkan ke MainActivity
        val token = sharedPreferencesHelper.getToken()
        if (!token.isNullOrEmpty()) {
            // Token ada, arahkan ke MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Jangan biarkan kembali ke halaman login
            return // Hentikan eksekusi lebih lanjut
        }

        // Proses login jika token belum ada
        val laundryRepository = LaundryRepository(ApiClient, application)
        val factory = UserViewModelFactory(application, laundryRepository)
        userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        userViewModel.loginResult.observe(this) { result ->
            result.onSuccess { loginResponse ->
                loginResponse.data?.let { data ->
                    data.user?.users_id?.let { userId ->
                        sharedPreferencesHelper.saveUserId(userId)
                    }
                    data.token?.let { token ->
                        sharedPreferencesHelper.saveToken(token)
                    }
                    data.user?.accountStatusId?.let { accountStatusId ->
                        sharedPreferencesHelper.saveAccountStatus(accountStatusId)
                    }
                    data.user?.storeName?.let { storeName ->
                        sharedPreferencesHelper.saveStoreName(storeName)
                    }
                    data.user?.storeAddress?.let { storeAddress ->
                        sharedPreferencesHelper.saveStoreAddress(storeAddress)
                    }
                    data.user?.name?.let { userName ->
                        sharedPreferencesHelper.saveUsername(userName)
                    }
                    data.user?.email?.let { email ->
                        sharedPreferencesHelper.saveEmail(email)
                    }
                    data.user?.phoneNumber?.let { phoneNumber ->
                        sharedPreferencesHelper.savePhoneNumber(phoneNumber)
                    }

                    val storeName = sharedPreferencesHelper.getStoreName()
                    val storeAddress = sharedPreferencesHelper.getStoreAddress()

                    Log.d("LoginActivity", "Store Name from SharedPreferences: $storeName")
                    Log.d("LoginActivity", "Store Address from SharedPreferences: $storeAddress")
                }
                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.onFailure { exception ->
                Toast.makeText(this, "Login Gagal: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (!isValidLoginInput(email, password)) {
                Toast.makeText(this, "Email atau Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Panggil metode login
            userViewModel.loginUser(email, password)
        }

        binding.tvFogetPassword.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
            finish()
        }

        binding.tvDaftar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun isValidLoginInput(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }
}
