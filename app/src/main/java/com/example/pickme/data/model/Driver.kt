package com.example.pickme.data.model

data class Driver(
    var id: String = "",
    var name: String = "", // changed from firstName
    var surname: String = "", // changed from lastName
    var phone: String = "",
    var emergencyNumber: String = "",
    var password: String = "",
    var carPlate: String = "",
    var carPhoto: String = "", // changed from carPhotoUrl
    var photo: String = "", // changed from driverPhotoUrl
    var driverLicense: String = "",
    val role: Int = 1,
    var verified: Boolean = false
)