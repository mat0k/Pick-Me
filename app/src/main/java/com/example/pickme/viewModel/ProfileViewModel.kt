package com.example.pickme.viewModel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pickme.data.model.Passenger
import com.example.pickme.data.model.User
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
    var name = mutableStateOf("")
    var surname = mutableStateOf("")
    var phone = mutableStateOf("")
    var emergencyNumber = mutableStateOf("")
    var photoUrl = mutableStateOf("")
    var loading = mutableStateOf(false)
    var photoChanged = mutableStateOf(false)
    private var newPhotoUrl = mutableStateOf("")
    private val authRepository = AuthRepository()
    val currentPassengerId = sharedPref.getString("lastUserId", "") ?: ""

    fun saveProfileData() {
        viewModelScope.launch {
            val currentPassenger = authRepository.getPassengerData(currentPassengerId)

            if (photoChanged.value) {
                newPhotoUrl.value = authRepository.uploadImageToFirebase(Uri.parse(photoUrl.value))
            }
            val updatedPassenger = Passenger(
                id = currentPassengerId,
                name = name.value,
                surname = surname.value,
                phone = phone.value,
                password = currentPassenger?.password ?: "",
                photoUrl = newPhotoUrl.value,
                emergencyNumber = emergencyNumber.value
            )
            authRepository.updatePassenger(updatedPassenger)
        }
    }

    fun loadProfileData() {
        viewModelScope.launch {
            val passenger = authRepository.getPassengerData(currentPassengerId)
            if (passenger != null) {
                name.value = passenger.name
                surname.value = passenger.surname
                phone.value = passenger.phone
                emergencyNumber.value = passenger.emergencyNumber
                photoUrl.value = passenger.photoUrl
            }
        }
    }

}