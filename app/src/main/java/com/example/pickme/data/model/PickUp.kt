package com.example.pickme.data.model

import com.google.android.gms.maps.model.LatLng

data class PickUp(
    var id: String = "",
    val pickUpTitle: String = "",
    val targetTitle: String = "",
    val pickUpLatLng: LatLng = LatLng(0.0, 0.0),
    val targetLatLng: LatLng = LatLng(0.0, 0.0),
    val distance: Double = 0.0,
    val dateAndTime: String = "",
    val passengerId: String = "",
    val driverId : String = "",
    val price: Double? = 0.0
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", LatLng(0.0, 0.0), LatLng(0.0, 0.0), 0.0, "", "", "", 0.0)
}
