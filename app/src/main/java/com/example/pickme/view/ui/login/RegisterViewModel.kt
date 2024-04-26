package com.example.pickme.view.ui.login

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pickme.data.model.Driver
import com.example.pickme.data.model.Passenger
import com.example.pickme.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class RegisterViewModel : ViewModel() {

    var phoneNumber = mutableStateOf("")
        private set

    var emergencyNumber = mutableStateOf("")
        private set

    private val authRepository = AuthRepository()

    var firstName = mutableStateOf("")
        private set

    var lastName = mutableStateOf("")
        private set

    private var photo: MutableState<Uri?> = mutableStateOf(Uri.EMPTY)

    var password = mutableStateOf("")
        private set

    var confirmPassword = mutableStateOf("")
        private set

    var role = mutableIntStateOf(0)
        private set

    fun updateFirstName(newFirstName: String) {
        firstName.value = newFirstName
    }

    fun updateLastName(newLastName: String) {
        lastName.value = newLastName
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun updatePhoneNumber(newPhoneNumber: String) {
        phoneNumber.value = newPhoneNumber
    }

    fun updateEmergencyNumber(newEmergencyNumber: String) {
        emergencyNumber.value = newEmergencyNumber
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        confirmPassword.value = newConfirmPassword
    }

    fun updateRole(newRole: Int) {
        if (role.intValue != newRole)
            role.intValue = newRole
    }

    fun passwordsMatch(): Boolean {
        return password.value == confirmPassword.value
    }

    var carPlate = mutableStateOf("")
        private set

    var driverLicense = mutableStateOf("")
        private set

    var carPhoto: MutableState<Uri?> = mutableStateOf(Uri.EMPTY)
        private set

    fun updateCarPlate(newCarPlate: String) {
        carPlate.value = newCarPlate
    }

    fun updateDriverLicense(newDriverLicense: String) {
        driverLicense.value = newDriverLicense
    }

    fun updateCarPhoto(newCarPhoto: Uri) {
        carPhoto.value = newCarPhoto
    }


    fun inputsFilled(): Boolean {
        return firstName.value.isNotEmpty()
                && lastName.value.isNotEmpty()
                && phoneNumber.value.isNotEmpty()
                && emergencyNumber.value.isNotEmpty()
                && password.value.isNotEmpty()
                && confirmPassword.value.isNotEmpty()
                && passwordsMatch()
                && photo.value != null
    }

    fun updateProfilePicture(it: Uri) {
        photo.value = it
    }

    private fun registerPassenger(context: Context) {
        val newPassenger = Passenger(
            "",
            firstName.value,
            lastName.value,
            password.value,
            phoneNumber.value,
            photo.value.toString(),
            emergencyNumber.value
        )
        viewModelScope.launch(Dispatchers.IO) {
            val result = authRepository.addPassenger(newPassenger)
            if (result.isFailure) {
                val message = result.exceptionOrNull()?.message ?: "An error occurred"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerDriver(context: Context) {
        val newDriver = Driver(
            // ... existing code ...
        )
        viewModelScope.launch(Dispatchers.IO) {
            val result = authRepository.addDriver(newDriver)
            if (result.isFailure) {
                val message = result.exceptionOrNull()?.message ?: "An error occurred"
                launch(Dispatchers.Main) {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun register(context: Context) {
        if(role.intValue == 0) {
            registerPassenger(context)
        }
        else registerDriver(context)
    }
}

