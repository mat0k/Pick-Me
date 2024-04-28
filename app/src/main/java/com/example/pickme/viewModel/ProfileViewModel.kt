package com.example.pickme.viewModel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pickme.data.model.Passenger
import com.example.pickme.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ProfileViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ProfileViewModel(val context: Context) : ViewModel() {
    val sharedPref: SharedPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    var name = mutableStateOf(sharedPref.getString("name", "")!!)
    var surname = mutableStateOf(sharedPref.getString("surname", "") ?: "")
    var phone = mutableStateOf(sharedPref.getString("phone", "") ?: "")
    var emergencyNumber = mutableStateOf(sharedPref.getString("emergencyNumber", "") ?: "")
    var photoUrl = mutableStateOf(sharedPref.getString("photoUrl", "") ?: "")
    var loading = mutableStateOf(false)
    var photoChanged = mutableStateOf(false)
    var newPhotoUrl = mutableStateOf("")
    private val authRepository = AuthRepository()


    fun saveProfileData() {
        viewModelScope.launch {
            val editor = sharedPref.edit()
            editor.putString("name", name.value)
            editor.putString("surname", surname.value)
            editor.putString("phone", phone.value)
            editor.putString("emergencyNumber", emergencyNumber.value)

            if (photoChanged.value) {
                newPhotoUrl.value = authRepository.uploadImageToFirebase(Uri.parse(photoUrl.value))
                editor.putString("photoUrl", newPhotoUrl.value)
            }
            val updatedPassenger = Passenger(
                id = sharedPref.getString("id", "") ?: "",
                name = name.value,
                surname = surname.value,
                phone = phone.value,
                password = sharedPref.getString("hashedPassword", "") ?: "",
                photoUrl = newPhotoUrl.value,
                emergencyNumber = sharedPref.getString("emergencyNumber", "") ?: ""
            )
            authRepository.updatePassenger(updatedPassenger)
            editor.apply()
        }
    }

    fun loadProfileData() {
        name.value = sharedPref.getString("name", "") ?: ""
        surname.value = sharedPref.getString("surname", "") ?: ""
        phone.value = sharedPref.getString("phone", "") ?: ""
        emergencyNumber.value = sharedPref.getString("emergencyNumber", "") ?: ""
    }
}