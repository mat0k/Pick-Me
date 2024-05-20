package com.example.pickme.data.repository

import com.example.pickme.data.model.LocalTrip
import com.google.firebase.Firebase
import com.google.firebase.database.database

class TripsRepository {
    private val ref = Firebase.database.getReference("Trips")

    fun joinTrip(tripId: String, passengerId: String) {
        ref.child(tripId).child("passengerIds").push().setValue(passengerId)
    }

    fun addTrip(trip: LocalTrip) {
    }
}