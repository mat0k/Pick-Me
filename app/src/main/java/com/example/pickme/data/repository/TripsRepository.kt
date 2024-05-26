package com.example.pickme.data.repository

import android.util.Log
import com.example.pickme.data.model.LocalTrip
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class TripsRepository {
    private val ref = Firebase.database.getReference("Trips")

    fun joinTrip(tripId: String, passengerId: String) {
        ref.child(tripId).child("passengerIds").push().setValue(passengerId)
    }

    suspend fun getTripTitle(tripId: String): String? {
        return try {
            val snapshot = ref.child(tripId).child("title").get().await()
            snapshot.value as? String
        } catch (e: Exception) {
            Log.e("TripsRepository", "Failed to get trip title", e)
            null
        }
    }
}