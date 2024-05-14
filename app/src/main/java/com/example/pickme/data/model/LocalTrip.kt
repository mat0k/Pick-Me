package com.example.pickme.data.model

data class LocalTrip(
    val id: String = "",
    val driverId: String,
    val title: String,
    val seats: Int,
    val starting: String,
    val end: String,
    val startingLatLng: com.google.android.gms.maps.model.LatLng,
    val destinationLatLng: com.google.android.gms.maps.model.LatLng,
    val date: String,
    val time: String,
    val tripDistance: Double,
    val verified: Boolean
)