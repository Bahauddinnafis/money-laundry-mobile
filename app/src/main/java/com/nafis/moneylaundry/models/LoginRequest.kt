package com.nafis.moneylaundry.models

import android.hardware.biometrics.BiometricManager.Strings

data class LoginRequest(
    val email: String,
    val password: String
)