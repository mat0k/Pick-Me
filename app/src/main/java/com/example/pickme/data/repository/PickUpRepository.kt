package com.example.pickme.data.repository

import com.example.pickme.data.model.LocalPickUp
import com.example.pickme.data.model.PickUp
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

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

    private fun mapToPickUp(map: Map<String, Any>): PickUp {
        val pickUpLatLngMap = map["pickUpLatLng"] as Map<String, Double>
        val targetLatLngMap = map["targetLatLng"] as Map<String, Double>

        return PickUp(
            id = map["id"] as String,
            passengerId = map["passengerId"] as String,
            pickUpTitle = map["pickUpTitle"] as String,
            targetTitle = map["targetTitle"] as String,
            pickUpLatLng = LatLng(pickUpLatLngMap["latitude"]!!, pickUpLatLngMap["longitude"]!!),
            targetLatLng = LatLng(targetLatLngMap["latitude"]!!, targetLatLngMap["longitude"]!!),
            distance = map["distance"] as Double,
            dateAndTime = map["dateAndTime"] as String
        )
    }


    fun getLivePickUps(): LiveData<List<PickUp>> {
        val myRef = database.getReference("PickUps")
        val liveData = MutableLiveData<List<PickUp>>()

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val pickUps = mutableListOf<PickUp>()
                for (postSnapshot in dataSnapshot.children) {
                    val pickUpMap = postSnapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                    if (pickUpMap != null) {
                        val pickUp = mapToPickUp(pickUpMap)
                        pickUps.add(pickUp)
                    }
                }
                liveData.value = pickUps
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        }
        myRef.addValueEventListener(postListener)

        return liveData
    }
}