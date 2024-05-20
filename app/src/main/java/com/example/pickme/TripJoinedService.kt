package com.example.pickme

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.pickme.notifications.TripNotificationService
import com.example.pickme.viewModel.PassengerViewModel
import com.google.firebase.Firebase
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
    private lateinit var passengerViewModel: PassengerViewModel // Add this line

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        ref = Firebase.database.getReference("Trips")
        tripNotificationService = TripNotificationService(this)
        passengerViewModel = PassengerViewModel() // Initialize the ViewModel
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE)
        val currentId = sharedPreferences.getString("lastUserId", "")
        var lastKnownTrips = listOf<FirebaseTrip>()
        val tripsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val trips = snapshot.children.mapNotNull { it.getValue(FirebaseTrip::class.java) }
                val currentUserTrips = trips.filter { it.driverId == currentId }
                currentUserTrips.forEach { currentTrip ->
                    val lastKnownTrip = lastKnownTrips.find { it.id == currentTrip.id }
                    currentTrip.passengerIds.forEach { passengerId ->
                        if (lastKnownTrip == null || !lastKnownTrip.passengerIds.contains(passengerId)) {
                            // New passenger joined, get their info and show notification
                            CoroutineScope(Dispatchers.IO).launch {
                                val passenger = passengerViewModel.getDriverInfo(passengerId)
                                val time = currentTrip.startTime
                                tripNotificationService.showNotification("${passenger.firstName} ${passenger.lastName}", time)
                            }
                        }
                    }
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
    val status: String = ""
)