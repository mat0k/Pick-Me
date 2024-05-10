package com.example.pickme.view.ui.driver

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pickme.data.model.User
import com.example.pickme.data.model.UserDatabaseHelper

class DriverProfileVMFactory(private val context: Context): ViewModelProvider.Factory {
    override  fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DriverProfileVM::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DriverProfileVM(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
class DriverProfileVM(val context: Context): ViewModel() {
    val sharedPref: SharedPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    val currentId = sharedPref.getString("lastUserId", "") ?: ""

    private val userDatabaseHelper = UserDatabaseHelper(context)
    val users: List<User> = userDatabaseHelper.getAllUsers()

}