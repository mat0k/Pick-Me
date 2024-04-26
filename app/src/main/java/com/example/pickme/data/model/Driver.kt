package com.example.pickme.data.model

import android.net.Uri

data class Driver(
    var id: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var phone: String = "",
    var password: String = "",
    var carPlate: String = "",
    var carPhoto: Uri? = null,
    var driverPhoto: Uri? = null,
    var driverLicense: String = "",
    var verified: Boolean = false
)