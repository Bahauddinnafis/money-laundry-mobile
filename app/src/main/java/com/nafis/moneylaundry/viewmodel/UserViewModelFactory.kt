package com.nafis.moneylaundry.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nafis.moneylaundry.repository.LaundryRepository

class UserViewModelFactory(
    private val application: Application,
    private val laundryRepository: LaundryRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(application, laundryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


