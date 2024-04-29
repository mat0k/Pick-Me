package com.example.pickme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.pickme.view.ui.driver.DriverView
import com.example.pickme.view.ui.login.LoginView
import com.example.pickme.view.ui.passenger.PassengerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val name = sharedPref.getString("name", null)
        val surname = sharedPref.getString("surname", null)
        val phone = sharedPref.getString("phone", null)
        val carPlate = sharedPref.getString("carPlate", null)

        if(name != null && surname !=null && phone != null) {
            if(carPlate != null) {
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

