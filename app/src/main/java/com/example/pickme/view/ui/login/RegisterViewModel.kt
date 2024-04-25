package com.example.pickme.view.ui.login


import OTPViewModel
import android.net.Uri
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
                && password.value.isNotEmpty()
                && confirmPassword.value.isNotEmpty()
                && passwordsMatch()
                && photo.value != null
    }

    fun updateProfilePicture(it: Uri) {
        photo.value = it
    }

    fun registerPassenger() {
        val newPassenger = Passenger(
            0,
            firstName.value,
            lastName.value,
            password.value,
            phoneNumber.value,
            photo.value.toString(),
            emergencyNumber.value
        )
        authRepository.addPassenger(newPassenger)
    }

    fun registerDriver() {
        val newDriver = Driver(
            firstName = firstName.value,
            lastName = lastName.value,
            password = password.value,
            phone = phoneNumber.value,
            carPlate = carPlate.value,
            carPhoto = carPhoto.value.toString(),
            driverPhoto = photo.value.toString(),
            driverLicense = driverLicense.value,
            verified = false
        )
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.addDriver(newDriver)
        }

    }

    fun register() {
        if(role.intValue == 0) registerPassenger()
        else registerDriver()
    }
}

