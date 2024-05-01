package com.example.pickme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.pickme.data.model.UserDatabaseHelper
import com.example.pickme.view.ui.driver.DriverView
import com.example.pickme.view.ui.login.LoginView
import com.example.pickme.view.ui.passenger.PassengerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val lastUserId = sharedPref.getString("lastUserId", null)
        val userDatabaseHelper = UserDatabaseHelper(this)

        if(lastUserId != null) {
            val userRole = userDatabaseHelper.getUserRole(lastUserId)
            if(userRole == 1) {
                val intent = Intent(applicationContext, DriverView::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(applicationContext, PassengerView::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            val intent = Intent(applicationContext, LoginView::class.java)
            startActivity(intent)
            finish()
        }
    }
}