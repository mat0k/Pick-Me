package com.example.pickme.view.ui.driver

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pickme.data.model.Driver
import com.example.pickme.data.model.User
import com.example.pickme.data.model.UserDatabaseHelper
import com.example.pickme.data.repository.AuthRepository
import kotlinx.coroutines.launch

class DriverProfileVMFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DriverProfileVM::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DriverProfileVM(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class DriverProfileVM(val context: Context) : ViewModel() {
    val sharedPref: SharedPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    val currentId = sharedPref.getString("lastUserId", "") ?: ""
    private val authRepository = AuthRepository()
    private val userDatabaseHelper = UserDatabaseHelper(context)
    val users: List<User> = userDatabaseHelper.getAllUsers()
    var name = mutableStateOf("")
    var surname = mutableStateOf("")
    var phone = mutableStateOf("")
    var emergencyNumber = mutableStateOf("")
    var photoUrl = mutableStateOf("")
    var loading = mutableStateOf(false)
    var photoChanged = mutableStateOf(false)
    private var newPhotoUrl = mutableStateOf("")
    fun loadProfileData() {
        viewModelScope.launch {
            val driver = authRepository.getDriverData(currentId)
            driver?.let {
                name = mutableStateOf(it.name)
                surname = mutableStateOf(it.surname)
                phone = mutableStateOf(it.phone)
                emergencyNumber = mutableStateOf(it.emergencyNumber)
                photoUrl = mutableStateOf(it.photo)
            }
        }
    }

    fun saveProfileData() {
        viewModelScope.launch {
            val driver = authRepository.getDriverData(currentId)

            if (photoChanged.value) {
                newPhotoUrl.value = authRepository.uploadImageToFirebase(Uri.parse(photoUrl.value))
            }
            val updatedDriver = Driver(
                id = currentId,
                name = name.value,
                surname = surname.value,
                phone = phone.value,
                password = driver?.password ?: "",
                photo = newPhotoUrl.value,
                emergencyNumber = emergencyNumber.value,
                carPlate = driver?.carPlate ?: "",
                carPhoto = driver?.carPhoto ?: "",
                driverLicense = driver?.driverLicense ?: "",
            )
            authRepository.updateDriver(updatedDriver)
        }
    }
}