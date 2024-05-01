package com.example.pickme.view.ui.login

import OTPViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pickme.R
import com.example.pickme.ui.passenger.ui.theme.PickMeUpTheme
import com.example.pickme.view.ui.driver.DriverView
import com.example.pickme.view.ui.passenger.PassengerView

class LoginView : ComponentActivity() {
    private lateinit var registerViewModel: RegisterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerViewModel =
            ViewModelProvider(this, RegisterViewModelFactory())[RegisterViewModel::class.java]
        setContent {
            PickMeUpTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(navController)
                        }
                        composable("register") {
                            RegisterScreen(navController, registerViewModel)
                        }
                        composable("otp") {
                            OTP(navController, registerViewModel, this@LoginView)
                        }
                        composable("driver") {
                            DriverInfo(navController, registerViewModel)
                        }
                    }
                }
            }
        }

    }

}

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current // Capture the context here
    val viewModelFactory = remember { LoginViewModelFactory(context) }
    val viewModel = viewModel<LoginViewModel>(factory = viewModelFactory)
    var loggingIn by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.loginResult.collect { loginResult ->
            if (loginResult == true) {
                val intent = when (viewModel.getUserRole()) {
                    0 -> Intent(context, PassengerView::class.java)
                    else -> Intent(context, DriverView::class.java)
                }
                context.startActivity(intent)
            } else if (loginResult == false) {
                Toast.makeText(context, "Invalid phone number or password", Toast.LENGTH_SHORT)
                    .show()
                loggingIn = false
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.login),
            fontSize = 36.sp,
        )

        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = viewModel.phoneNumber,
            onValueChange = { viewModel.updatePhoneNumber(it) },
            label = { Text(text = stringResource(R.string.phone_number)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text(text = stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row {
            FilterChip(selected = viewModel.role == 0,
                onClick = {
                    if (viewModel.role != 0) viewModel.updateRole(0)
                },
                label = { Text("Passenger") },
                leadingIcon = {
                    if (viewModel.role == 0) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(selected = viewModel.role == 1,
                onClick = {
                    if (viewModel.role != 1) viewModel.updateRole(1)
                },
                label = { Text("Driver") },
                leadingIcon = {
                    if (viewModel.role == 1) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
        ) {
            ElevatedButton(
                onClick = { navController.navigate("register") }) {
                Text(stringResource(R.string.register))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    loggingIn = true
                    viewModel.login()
                },
                enabled = viewModel.inputsFilled() && !loggingIn
            ) {
                Text(stringResource(R.string.login))
            }
            Spacer(modifier = Modifier.width(20.dp))
            if (loggingIn) {
                CircularProgressIndicator()
            }
        }

    }
}

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel) {
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateProfilePicture(it)
        }
    }
    var registerButtonPressed by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.register),
            fontSize = 36.sp,
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(value = viewModel.firstName.value, onValueChange = {
            viewModel.updateFirstName(it)
        }, placeholder = {
            Text(
                stringResource(R.string.first_name)
            )
        })
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = viewModel.lastName.value, onValueChange = {
            viewModel.updateLastName(it)
        }, placeholder = {
            Text(
                stringResource(R.string.last_name)
            )
        })

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.phoneNumber.value,
            onValueChange = { viewModel.updatePhoneNumber(it) },
            placeholder = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.emergencyNumber.value,
            onValueChange = { viewModel.updateEmergencyNumber(it) },
            placeholder = { Text("Emergency Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.password.value,
            onValueChange = { viewModel.updatePassword(it) },
            placeholder = { Text(text = stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.confirmPassword.value,
            onValueChange = { viewModel.updateConfirmPassword(it) },
            placeholder = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = !viewModel.passwordsMatch()
        )



        Spacer(modifier = Modifier.height(16.dp))
        Row {
            FilterChip(selected = viewModel.role.intValue == 0,
                onClick = { viewModel.updateRole(0) },
                label = { Text("Passenger") },
                leadingIcon = {
                    if (viewModel.role.intValue == 0) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(selected = viewModel.role.intValue == 1,
                onClick = { viewModel.updateRole(1) },
                label = { Text("Driver") },
                leadingIcon = {
                    if (viewModel.role.intValue == 1) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = {
                pickImageLauncher.launch("image/*")
            }) {
                Text("Add profile picture")
            }
            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    registerButtonPressed = true
                    if (viewModel.role.intValue == 0) {
                        navController.navigate("otp")
                    } else {
                        navController.navigate("driver")
                    }
                },
                enabled = viewModel.inputsFilled() && !registerButtonPressed
            ) {
                Text(stringResource(R.string.register))
            }
            if (registerButtonPressed) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun DriverInfo(navController: NavController, viewModel: RegisterViewModel) {
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateCarPhoto(it)
        }
    }
    Column(
        modifier = Modifier
            .padding(50.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Enter your information",
            fontSize = 36.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.carPlate.value,
            onValueChange = {
                viewModel.updateCarPlate(it)
            },
            placeholder = { Text("Car Plate") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                pickImageLauncher.launch("image/*")
            }
        ) {
            Text("Add car photo")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.driverLicense.value,
            onValueChange = {
                viewModel.updateDriverLicense(it)
            },
            placeholder = { Text("Driver License") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                navController.navigate("otp")
            },
            enabled = viewModel.carPlate.value.isNotEmpty() && viewModel.driverLicense.value.isNotEmpty() && viewModel.carPhoto.value != Uri.EMPTY
        ) {
            Text("Register")
        }
    }

}

@Composable
fun OTP(
    navController: NavController,
    registerViewModel: RegisterViewModel,
    activity: ComponentActivity
) {
    val otpViewModel = viewModel<OTPViewModel>()
    LaunchedEffect(key1 = true) {
        otpViewModel.authenticate("+961 ${registerViewModel.phoneNumber.value}", activity) {
        }
    }
    Column(
        modifier = Modifier
            .padding(50.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter the 6-digit code we sent to ${registerViewModel.phoneNumber.value}",
            fontSize = 26.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = otpViewModel.otp.value,
            onValueChange = { otpViewModel.otp.value = it },
            placeholder = { Text("OTP") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = otpViewModel.otpError.value.isNotEmpty()
        )

        if (otpViewModel.otpError.value.isNotEmpty()) {
            Text(
                text = otpViewModel.otpError.value,
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (otpViewModel.isLoading.value) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    otpViewModel.verifyOTP { verificationSuccessful ->
                        if (verificationSuccessful) {
                            registerViewModel.register(activity)
                            navController.navigate("login")
                        }
                    }
                },
                enabled = otpViewModel.otp.value.length == 6
            ) {
                Text("Verify")
            }
        }
    }
}