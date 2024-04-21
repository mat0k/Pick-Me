package com.example.pickme.view.ui.login


import  android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pickme.data.model.Passenger
import com.example.pickme.data.repository.RegisterRepository

class RegisterViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class RegisterViewModel(private val context: Context) : ViewModel() {

    private val registerRepository = RegisterRepository(context)

    var firstName = mutableStateOf("")
        private set

    var lastName = mutableStateOf("")
        private set

    var photo: Uri? = null
        private set

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

    fun inputsFilled(): Boolean {
        return firstName.value.isNotEmpty() && lastName.value.isNotEmpty() && password.value.isNotEmpty() && confirmPassword.value.isNotEmpty() && passwordsMatch() && photo != null
    }

    fun updateProfilePicture(it: Uri) {
        photo = it
    }

    fun register() {
        val newPassenger = Passenger(
            0,
            firstName.value,
            lastName.value,
            password.value,
            "1234567890",
            photo.toString(),
            "0987654321"
        )
        registerRepository.addPassenger(newPassenger)

    }


}

