package com.example.pickme.data.model

import com.google.android.gms.maps.model.LatLng

data class LocalPickUp(
    val id: Int = 0,
    val pickUpTitle: String,
    val targetTitle: String,
    val pickUpLatLng: LatLng,
    val targetLatLng: LatLng,
    val distance: Double,
    val dateAndTime: String,
    val passengerId: String? = null,
    val driverId: String? = null,
    val price: Double?,
)