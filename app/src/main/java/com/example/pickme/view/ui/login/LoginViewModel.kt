package com.example.pickme.view.ui.login


import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pickme.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModelFactory(private val sharedPref: SharedPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(sharedPref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class LoginViewModel(private val sharedPref: SharedPreferences) : ViewModel() {
    private val authRepository = AuthRepository()
    var phoneNumber by mutableStateOf("")
        private set
    var password by mutableStateOf("")
    var role by mutableIntStateOf(0)
    val loginResult = MutableStateFlow<Boolean?>(null)
    fun inputsFilled(): Boolean {
        return phoneNumber.isNotEmpty() && password.isNotEmpty()
    }

    fun login() {
        viewModelScope.launch {
            if (role == 0) {
                val result = authRepository.loginAsPassenger(phoneNumber, password)
                if (result.isSuccess) {
                    val passenger = result.getOrNull()
                    with(sharedPref.edit()) {
                        putString("id", passenger?.id)
                        putString("name", passenger?.name)
                        putString("surname", passenger?.surname)
                        putString("hashedPassword", passenger?.password)
                        putString("phone", passenger?.phone)
                        putString("photoUrl", passenger?.photoUrl)
                        putString("emergencyNumber", passenger?.emergencyNumber)
                        apply()
                    }
                    loginResult.value = true
                } else {
                    loginResult.value = false
                }
            } else {
                val driver = authRepository.loginAsDriver(phoneNumber, password)
                if (driver != null) {
                    with(sharedPref.edit()) {
                        putString("id", driver.id)
                        putString("name", driver.firstName)
                        putString("surname", driver.lastName)
                        putString("hashedPassword", driver.password)
                        putString("phone", driver.phone)
                        putString("carPlate", driver.carPlate)
                        putString("driverLicense", driver.driverLicense)
                        putBoolean("verified", driver.verified)
                        putString("carPhotoUri", driver.carPhoto.toString())
                        putString("driverPhotoUri", driver.driverPhoto.toString())
                        apply()
                    }
                    loginResult.value = true
                }else {
                    loginResult.value = false
                }
            }
        }
    }

    fun updatePhoneNumber(phoneNumber: String) {
        this.phoneNumber = phoneNumber
    }

    fun updatePassword(password: String) {
        this.password = password
    }

    fun updateRole(role: Int) {
        this.role = role
    }

    fun getUserRole(): Int {
        return role
    }
}