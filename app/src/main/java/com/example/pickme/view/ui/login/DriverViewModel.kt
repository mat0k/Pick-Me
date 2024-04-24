package com.example.pickme.view.ui.login

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


class DriverViewModel : ViewModel() {
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


}