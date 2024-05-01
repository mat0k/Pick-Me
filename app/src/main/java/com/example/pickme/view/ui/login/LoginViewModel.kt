package com.example.pickme.view.ui.login


import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pickme.data.model.User
import com.example.pickme.data.model.UserDatabaseHelper
import com.example.pickme.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class LoginViewModel(private val context: Context) : ViewModel() {
    private val authRepository = AuthRepository()
    private val userDatabaseHelper = UserDatabaseHelper(context)
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)

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
                    val user = result.getOrNull()
                    user?.let {
                        userDatabaseHelper.addUser(it)
                        sharedPreferences.edit().putString("lastUserId", it.id).apply()
                    }
                    loginResult.value = true
                } else {
                    loginResult.value = false
                }
            } else {
                val result = authRepository.loginAsDriver(phoneNumber, password)
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    user?.let {
                        userDatabaseHelper.addUser(it)
                        sharedPreferences.edit().putString("lastUserId", it.id).apply()
                    }
                    loginResult.value = true
                } else {
                    loginResult.value = false
                }
            }
        }
    }

    fun loginAsUser(user: User) {
        viewModelScope.launch {
            userDatabaseHelper.addUser(user)
            sharedPreferences.edit().putString("lastUserId", user.id).apply()
            loginResult.value = true
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