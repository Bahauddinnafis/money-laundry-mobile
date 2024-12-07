package com.nafis.moneylaundry.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nafis.moneylaundry.SharedPreferencesHelper
import com.nafis.moneylaundry.models.auth.LoginResponse
import com.nafis.moneylaundry.models.packageLaundry.PaketLaundryModel
import com.nafis.moneylaundry.repository.AuthRepository
import com.nafis.moneylaundry.repository.LaundryRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application, private val laundryRepository: LaundryRepository) : AndroidViewModel(application) {

    private val preferencesHelper = SharedPreferencesHelper(application)
    private val authRepository = AuthRepository()

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> get() = _loginResult

    private val _paketLaundryList = MutableLiveData<List<PaketLaundryModel>>()
    val paketLaundryList: LiveData<List<PaketLaundryModel>> get() = _paketLaundryList

    private val _updatePaketResult = MutableLiveData<Result<PaketLaundryModel>>()
    val updatePaketResult: LiveData<Result<PaketLaundryModel>> get() = _updatePaketResult

    private val _deletePaketResult = MutableLiveData<Result<Boolean>>()
    val deletePaketResult: LiveData<Result<Boolean>> = _deletePaketResult

    // Mengambil ID pengguna
    fun getUserId(): Int = preferencesHelper.getUserId()

    // Mengambil status akun pengguna
    fun getUserAccountStatus(): Int = preferencesHelper.getAccountStatus()

    // Menyimpan userId ke SharedPreferences
    fun saveUserId(userId: Int) {
        preferencesHelper.saveUserId(userId)
    }

    // Menyimpan token ke SharedPreferences
    fun saveToken(token: String) {
        preferencesHelper.saveToken(token)
    }

    // Menyimpan package_laundry_id ke SharedPreferences
    fun savePackageLaundryId(packageLaundryId: Int) {
        preferencesHelper.savePackageLaundryId(packageLaundryId)
    }

    // Mengambil package_laundry_id dari SharedPreferences
    fun getPackageLaundryId(): Int {
        return preferencesHelper.getPackageLaundryId()
    }

    // Login pengguna
    fun loginUser (email: String, password: String) {
        authRepository.loginUser (email, password) { result ->
            _loginResult.postValue(result)
            result.onSuccess { loginResponse ->
                loginResponse.data?.let { data ->
                    data.user?.let { user ->
                        preferencesHelper.saveToken(data.token ?: "")
                        preferencesHelper.saveUserId(user.users_id ?: -1)
                        preferencesHelper.saveAccountStatus(user.accountStatusId ?: 1)
                    }
                }
            }
        }
    }

    // Menambahkan paket laundry
    fun addPaketLaundry(paketLaundry: PaketLaundryModel) {
        val token = preferencesHelper.getToken()
        val userId = preferencesHelper.getUserId() // Pastikan Anda mendapatkan userId dengan benar

        if (token.isNullOrEmpty()) {
            Log.d("addPaketLaundry", "Token is empty, cannot add package.")
            return
        }

        laundryRepository.createPackageLaundry(userId, token, paketLaundry) { result ->
            result.onSuccess { response ->
                // Pastikan untuk mengakses packageLaundryId dari data
                val packageLaundryId = response.data?.packageLaundryId ?: -1
                preferencesHelper.savePackageLaundryId(packageLaundryId)
                val currentList = _paketLaundryList.value ?: emptyList()
                _paketLaundryList.value = currentList + paketLaundry
                Log.d("addPaketLaundry", "Successfully added package with ID: $packageLaundryId")
            }.onFailure { throwable ->
                Log.e("addPaketLaundry", "Failed to add package: ${throwable.message}")
            }
        }
    }

    // Mengambil paket laundry dari repository
    fun fetchPaketLaundry() {
        val userId = getUserId()
        val token = preferencesHelper.getToken()

        if (token.isNullOrEmpty()) {
            return
        }

        laundryRepository.fetchPaketLaundry(userId, token) { result ->
            result.onSuccess { paketLaundryList ->
                _paketLaundryList.postValue(paketLaundryList)
                val canAdd = canAddPaketLaundry()
                Log.d("fetchPaketLaundry", "Can add package: $canAdd")
            }
            result.onFailure { throwable ->
                Log.e("fetchPaketLaundry", "Failed to fetch data: ${throwable.message}")
            }
        }
    }

    // Memeriksa apakah pengguna dapat menambahkan paket laundry
    fun canAddPaketLaundry(): Boolean {
        val accountStatus = getUserAccountStatus()
        val currentPaketCount = _paketLaundryList.value?.size ?: 0

        Log.d("canAddPaketLaundry", "accountStatus: $accountStatus, currentPaketCount: $currentPaketCount")

        return !(accountStatus == 1 && currentPaketCount >= 3)
    }

    // Memperbarui paket laundry
    fun updatePaketLaundry(paketLaundry: PaketLaundryModel) {
        val token = preferencesHelper.getToken()
        if (token.isNullOrEmpty()) {
            _updatePaketResult.value = Result.failure(Exception("Token is empty"))
            return
        }

        // Pastikan Anda mengirimkan ID yang benar
        laundryRepository.updatePackageLaundry(paketLaundry.package_laundry_id, token, paketLaundry) { result ->
            _updatePaketResult.value = result
        }
    }

    // Mendapatkan paket laundry berdasarkan ID
    fun getPackageLaundryById(packageLaundryId: Int): LiveData<Result<PaketLaundryModel>> {
        val result = MutableLiveData<Result<PaketLaundryModel>>()
        val token = preferencesHelper.getToken() // Ambil token dari SharedPreferences

        if (token.isNullOrEmpty()) {
            result.value = Result.failure(Exception("Token is empty"))
            return result
        }

        viewModelScope.launch {
            laundryRepository.getPackageLaundryById(packageLaundryId, token) { callbackResult ->
                result.value = callbackResult
            }
        }
        return result
    }

    fun deletePaketLaundry(packageLaundryId: Int) {
        val token = preferencesHelper.getToken()
        if (token.isNullOrEmpty()) {
            _deletePaketResult.value = Result.failure(Exception("Token is empty"))
            return
        }

        laundryRepository.deletePackageLaundry(packageLaundryId, token) { result ->
            _deletePaketResult.value = result
        }
    }
}



