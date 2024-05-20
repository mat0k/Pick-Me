package com.example.pickme.data.model

import com.google.android.gms.maps.model.LatLng

data class LocalTrip(
    val id: String = "",
    val driverId: String = "",
    val title: String = "",
    val seats: Int = 0,
    val starting: String = "",
    val end: String = "",
    val startingLatLng: LatLng = LatLng(0.0, 0.0),
    val destinationLatLng: LatLng = LatLng(0.0, 0.0),
    val date: String = "",
    val time: String = "",
    val tripDistance: Double = 0.0,
    val verified: Boolean = false,
    val passengerIds: MutableList<String> = mutableListOf(),
) {
    constructor() : this("", "", "", 0, "", "", LatLng(0.0, 0.0), LatLng(0.0, 0.0), "", "", 0.0, false, mutableListOf())
}