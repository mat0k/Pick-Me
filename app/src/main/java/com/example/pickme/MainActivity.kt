package com.example.pickme

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pickme.ui.passenger.ui.theme.PickMeUpTheme
import com.example.pickme.view.ui.driver.DriverView
import com.example.pickme.view.ui.login.LoginView
import com.example.pickme.view.ui.passenger.PassengerView


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PickMeUpTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Button(onClick = {
                            val intent = Intent(applicationContext, LoginView::class.java)
                            startActivity(intent)
                            finish()
                        }) {
                            Text(
                                text = "Login Screen",
                                fontSize = 25.sp
                            )
                        }

                        Spacer(modifier = Modifier.padding(10.dp))

                        Button(onClick = {
                            val intent = Intent(applicationContext, PassengerView::class.java)
                            startActivity(intent)
                            finish()
                        }) {
                            Text(
                                text = "Passenger View",
                                fontSize = 25.sp
                            )
                        }

                        Spacer(modifier = Modifier.padding(10.dp))

                        Button(onClick = {
                            val intent = Intent(applicationContext, DriverView::class.java)
                            startActivity(intent)
                            finish()
                        }) {
                            Text(
                                text = "Driver View",
                                fontSize = 25.sp
                            )
                        }

                    }


                }
            }
        }
    }
}
