package com.nafis.moneylaundry.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nafis.moneylaundry.R
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

        val token = sharedPreferencesHelper.getToken()
        if (!token.isNullOrEmpty()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        val laundryRepository = LaundryRepository(ApiClient, application)
        val factory = UserViewModelFactory(application, laundryRepository)
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

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
                showCustomToastSuccess(this, "Login Berhasil!")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.onFailure { exception ->
                showCustomToastError(this, "Login Gagal!")
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (!isValidLoginInput(email, password)) {
                showCustomToastError(this, "Email atau Password tidak boleh kosong")
                return@setOnClickListener
            }

            userViewModel.isLoading.observe(this) { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }

            userViewModel.loginUser(email, password)

            userViewModel.loginResult.observe(this) { result ->
                result.onSuccess {
                    showCustomToastSuccess(this, "Login berhasil")
                }.onFailure {
                    showCustomToastError(this, "Login gagal: ${it.message}")
                }
            }
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
