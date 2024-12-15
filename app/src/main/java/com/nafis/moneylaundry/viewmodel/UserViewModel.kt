package com.nafis.moneylaundry.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nafis.moneylaundry.SharedPreferencesHelper
import com.nafis.moneylaundry.models.auth.LoginResponse
import com.nafis.moneylaundry.models.packageLaundry.PaketLaundryModel
import com.nafis.moneylaundry.repository.AuthRepository
import com.nafis.moneylaundry.repository.LaundryRepository

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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    fun getUserId(): Int = preferencesHelper.getUserId()

    fun getUserAccountStatus(): Int = preferencesHelper.getAccountStatus()

    fun savePackageLaundryId(packageLaundryId: Int) {
        preferencesHelper.savePackageLaundryId(packageLaundryId)
    }

    fun getPackageLaundryId(): Int {
        return preferencesHelper.getPackageLaundryId()
    }

    fun loginUser(email: String, password: String) {
        _isLoading.postValue(true)

        authRepository.loginUser(email, password) { result ->
            _isLoading.postValue(false)

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

    fun addPaketLaundry(paketLaundry: PaketLaundryModel) {
        val token = preferencesHelper.getToken()
        val userId = preferencesHelper.getUserId()

        if (token.isNullOrEmpty()) {
            Log.d("addPaketLaundry", "Token is empty, cannot add package.")
            return
        }

        laundryRepository.createPackageLaundry(userId, token, paketLaundry) { result ->
            result.onSuccess { response ->
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

    fun canAddPaketLaundry(): Boolean {
        val accountStatus = getUserAccountStatus()
        val currentPaketCount = _paketLaundryList.value?.size ?: 0

        Log.d("canAddPaketLaundry", "accountStatus: $accountStatus, currentPaketCount: $currentPaketCount")

        return !(accountStatus == 1 && currentPaketCount >= 3)
    }

    fun updatePaketLaundry(paketLaundry: PaketLaundryModel) {
        val token = preferencesHelper.getToken()
        if (token.isNullOrEmpty()) {
            _updatePaketResult.value = Result.failure(Exception("Token is empty"))
            return
        }

        laundryRepository.updatePackageLaundry(paketLaundry.package_laundry_id, token, paketLaundry) { result ->
            _updatePaketResult.value = result
        }
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



