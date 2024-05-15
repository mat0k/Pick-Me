package com.example.pickme.data.model

import com.google.android.gms.maps.model.LatLng

data class PickUp(
    val id: String = "",
    val pickUpTitle: String = "",
    val targetTitle: String = "",
    val pickUpLatLng: LatLng,
    val targetLatLng: LatLng,
    val distance: Double = 0.0,
    val dateAndTime: String = "",
    val passengerId: String = "",
    val driverId : String = ""
)