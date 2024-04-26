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

class ProfileViewModel (val context: Context) : ViewModel() {
    val sharedPref: SharedPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    var name = mutableStateOf(sharedPref.getString("name", "")!!)
    var surname = mutableStateOf(sharedPref.getString("surname", "") ?: "")
    var phone = mutableStateOf(sharedPref.getString("phone", "") ?: "")
    var emergencyNumber = mutableStateOf(sharedPref.getString("emergencyNumber", "") ?: "")
    private val authRepository = AuthRepository()
    fun saveProfileData() {
        with(sharedPref.edit()) {
            putString("name", name.value)
            putString("surname", surname.value)
            putString("phone", phone.value)
            apply()
        }

        val updatedPassenger = Passenger(
            id = sharedPref.getString("id", "") ?: "",
            name = name.value,
            surname = surname.value,
            phone = phone.value,
            password = sharedPref.getString("hashedPassword", "") ?: "",
            photoUrl = sharedPref.getString("photoUrl", "") ?: "",
            emergencyNumber = sharedPref.getString("emergencyNumber", "") ?: ""
        )

        // Call updatePassenger() with the updated Passenger object
        viewModelScope.launch {
            authRepository.updatePassenger(updatedPassenger)
        }
    }

    fun loadProfileData() {
        name.value = sharedPref.getString("name", "") ?: ""
        surname.value = sharedPref.getString("surname", "") ?: ""
        phone.value = sharedPref.getString("phone", "") ?: ""
    }
}