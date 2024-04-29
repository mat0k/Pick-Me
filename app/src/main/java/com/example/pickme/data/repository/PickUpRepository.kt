package com.example.pickme.data.repository

import com.example.pickme.data.model.LocalPickUp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PickUpRepository {
    private val database: FirebaseDatabase = Firebase.database

    fun addPickUp(pickUp: LocalPickUp, passengerId: String) {
        val myRef = database.getReference("PickUps")
        val pickUpObject = mapOf(
            "id" to myRef.push().key, // Generate unique ID
            "pickUpTitle" to pickUp.pickUpTitle,
            "targetTitle" to pickUp.targetTitle,
            "pickUpLatLng" to mapOf(
                "latitude" to pickUp.pickUpLatLng.latitude,
                "longitude" to pickUp.pickUpLatLng.longitude
            ),
            "targetLatLng" to mapOf(
                "latitude" to pickUp.targetLatLng.latitude,
                "longitude" to pickUp.targetLatLng.longitude
            ),
            "distance" to pickUp.distance,
            "dateAndTime" to pickUp.dateAndTime,
            "passengerId" to passengerId
        )
        myRef.child(pickUpObject["id"] as String).setValue(pickUpObject)
    }
}