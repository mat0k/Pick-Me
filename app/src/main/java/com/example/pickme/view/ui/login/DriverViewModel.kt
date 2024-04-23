package com.example.pickme.view.ui.login

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class DriverViewModel : ViewModel() {
    var car_plate = mutableStateOf("")
        private set

    var driver_license = mutableStateOf("")
        private set

    var car_photo: MutableState<Uri?> = mutableStateOf(Uri.EMPTY)
        private set

    fun updateCarPlate(newCarPlate: String) {
        car_plate.value = newCarPlate
    }

    fun updateDriverLicense(newDriverLicense: String) {
        driver_license.value = newDriverLicense
    }

    fun updateCarPhoto(newCarPhoto: Uri) {
        car_photo.value = newCarPhoto
    }


}