package com.example.pickme.data.model

data class Driver(
    var id: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var phone: String = "",
    var password: String = "",
    var carPlate: String = "",
    var carPhoto: String = "",
    var driverPhoto: String = "",
    var driverLicense: String = "",
    var verified: Boolean = false
)