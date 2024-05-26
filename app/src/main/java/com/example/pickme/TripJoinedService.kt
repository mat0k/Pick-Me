package com.example.pickme

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.pickme.notifications.TripNotificationService
import com.example.pickme.viewModel.PassengerViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TripJoinedService : Service() {
    private lateinit var ref: DatabaseReference
    private lateinit var tripNotificationService: TripNotificationService
    private lateinit var passengerViewModel: PassengerViewModel
    private lateinit var currentId: String
    override fun onBind(intent: Intent?): IBinder? {
        return null // This service doesn't provide binding
    }

    override fun onCreate() {
        super.onCreate()
        ref = com.google.firebase.Firebase.database.getReference("Trips")
        tripNotificationService = TripNotificationService(this)
        passengerViewModel = PassengerViewModel()
        currentId = getSharedPreferences("MyPref", MODE_PRIVATE).getString("lastUserId", "") ?: ""
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val knownTrips = mutableListOf<FirebaseTrip>()
        Log.d("TripJoinedService", "Service started.")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("TripJoinedService", "DataSnapshot received.")
                val trips = snapshot.children.mapNotNull { it.getValue(FirebaseTrip::class.java) }
                val currentDriverTrips = trips.filter { it.driverId == currentId }
                Log.d("TripJoinedService", "DataSnapshot received: ${snapshot.childrenCount} children.")
                val justJoinedTrips = currentDriverTrips.filter {
                    Log.d("TripJoinedService", "Processing trip: ${it.id}")
                    it.availableSeats < (knownTrips.find { knownTrip -> knownTrip.id == it.id }?.availableSeats
                        ?: 0)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    justJoinedTrips.forEach{trip ->
                        Log.d("TripJoinedService", "Sending Notification: ${trip.id}")
                        tripNotificationService.showNotification(trip.id)
                    }
                }
                knownTrips.clear()
                knownTrips.addAll(currentDriverTrips)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return START_STICKY
    }
}

data class FirebaseTrip(
    val id: String = "",
    val driverId: String = "",
    val passengerIds: List<String> = emptyList(),
    val startLocation: LatLngWrapper = LatLngWrapper(),
    val endLocation: LatLngWrapper = LatLngWrapper(),
    val startTime: Long = 0,
    val endTime: Long = 0,
    val price: Double = 0.0,
    val distance: Double = 0.0,
    val status: String = "",
    val availableSeats: Int = 0
)