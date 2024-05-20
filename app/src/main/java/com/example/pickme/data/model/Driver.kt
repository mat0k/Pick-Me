package com.example.pickme.data.model

data class Driver(
    var id: String = "",
    var name: String = "",
    var surname: String = "",
    var phone: String = "",
    var emergencyNumber: String = "",
    var password: String = "",
    var carPlate: String = "",
    var carPhoto: String = "",
    var photo: String = "",
    var driverLicense: String = "",
    val role: Int = 1,
    var verified: Boolean = false
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", "", "", "", "", "", "", "", 1, false)
}
