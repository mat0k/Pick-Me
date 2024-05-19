package com.example.pickme

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.pickme.data.model.LocalTrip
import com.example.pickme.notifications.TripNotificationService
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class TripJoinedService : Service() {
    private lateinit var ref: DatabaseReference
    private lateinit var tripNotificationService: TripNotificationService
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        ref = Firebase.database.getReference("Trips")
        tripNotificationService = TripNotificationService(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE)
        val currentId = sharedPreferences.getString("lastUserId", "")
        var lastKnownTrips = listOf<LocalTrip>()
        val tripsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val trips = snapshot.children.mapNotNull { it.getValue(LocalTrip::class.java) }
                val currentUserTrips = trips.filter { it.driverId == currentId }
                val justJoinedTrips =
                    currentUserTrips.filter { it.passengerIds.isNotEmpty() && lastKnownTrips.find { lastTrip -> lastTrip.id == it.id }?.passengerIds.isNullOrEmpty() }
                if (justJoinedTrips.isNotEmpty()) {
                    tripNotificationService.showNotification()
                }
                lastKnownTrips = currentUserTrips
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        }
        ref.addValueEventListener(tripsListener)
        return super.onStartCommand(intent, flags, startId)
    }
}