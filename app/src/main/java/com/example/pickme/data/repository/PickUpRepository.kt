package com.example.pickme.data.repository

import android.util.Log
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

    fun addPickUp(pickUp: PickUp) : String?{
        val myRef = database.getReference("PickUps")
        val id = myRef.push().key
        val pickUpObject = mapOf(
            "id" to id, // Generate unique ID
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
            "passengerId" to pickUp.passengerId,
            "driverId" to "",
            "price" to pickUp.price
        )
        myRef.child(pickUpObject["id"] as String).setValue(pickUpObject)
        return id
    }

    private fun mapToPickUp(map: Map<String, Any>): PickUp {
        val pickUpLatLngMap = map["pickUpLatLng"] as Map<String, Any>
        val targetLatLngMap = map["targetLatLng"] as Map<String, Any>

        val pickUpLat = (pickUpLatLngMap["latitude"] as Number).toDouble()
        val pickUpLng = (pickUpLatLngMap["longitude"] as Number).toDouble()

        val targetLat = (targetLatLngMap["latitude"] as Number).toDouble()
        val targetLng = (targetLatLngMap["longitude"] as Number).toDouble()

        val distance = (map["distance"] as Number).toDouble()

        return PickUp(
            id = map["id"] as String,
            passengerId = map["passengerId"] as String,
            pickUpTitle = map["pickUpTitle"] as String,
            targetTitle = map["targetTitle"] as String,
            pickUpLatLng = LatLng(pickUpLat, pickUpLng),
            targetLatLng = LatLng(targetLat, targetLng),
            distance = distance,
            dateAndTime = map["dateAndTime"] as String,
            price = map["price"] as Double,
            driverId = map["driverId"] as String
        )
    }

    fun getLivePickUps(): LiveData<List<PickUp>> {
        val myRef = database.getReference("PickUps")
        val liveData = MutableLiveData<List<PickUp>>()

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val pickUps = mutableListOf<PickUp>()
                for (postSnapshot in dataSnapshot.children) {
                    val pickUpMap =
                        postSnapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                    if (pickUpMap != null) {
                        val pickUp = mapToPickUp(pickUpMap)
                        pickUps.add(pickUp)
                        Log.d(
                            "PickUpRepository",
                            "PickUp Information: id = ${pickUp.id}, passengerId = ${pickUp.passengerId}, pickUpTitle = ${pickUp.pickUpTitle}, targetTitle = ${pickUp.targetTitle}, pickUpLatLng = ${pickUp.pickUpLatLng}, targetLatLng = ${pickUp.targetLatLng}, distance = ${pickUp.distance}, dateAndTime = ${pickUp.dateAndTime}, price = ${pickUp.price}, driverId = ${pickUp.driverId}"
                        )

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

    fun acceptPickUp(id: String, driverId: String) {
        val myRef = database.getReference("PickUps")
        myRef.child(id).child("driverId").setValue(driverId)

    }
}