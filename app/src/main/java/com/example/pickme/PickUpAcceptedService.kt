package com.example.pickme

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.pickme.data.model.Driver
import com.example.pickme.notifications.PickUpAcceptedNotificationService
import com.google.firebase.database.*

class PickUpAcceptedService : Service() {
    private lateinit var database: FirebaseDatabase
    private lateinit var pickUpsReference: DatabaseReference
    private lateinit var pickUpAcceptedNotificationService: PickUpAcceptedNotificationService
    private lateinit var driversReference: DatabaseReference

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        database = FirebaseDatabase.getInstance()
        pickUpsReference = database.getReference("PickUps")
        pickUpAcceptedNotificationService = PickUpAcceptedNotificationService(this)
        driversReference = database.getReference("Drivers")
        Log.d("PickUpAcceptedService", "Service created and database references initialized.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE)
        val currentId = sharedPreferences.getString("lastUserId", "")

        if (currentId.isNullOrEmpty()) {
            Log.e("PickUpAcceptedService", "Current user ID not found in SharedPreferences.")
            return START_NOT_STICKY
        }

        var lastKnownPickups = listOf<FirebasePickUp>()
        val pickUpsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("PickUpAcceptedService", "DataSnapshot received: ${snapshot.childrenCount} children.")
                val pickups = snapshot.children.mapNotNull { it.getValue(FirebasePickUp::class.java) }
                val currentUserPickups = pickups.filter { it.passengerId == currentId }
                val justAcceptedPickups = currentUserPickups.filter {
                    it.driverId.isNotEmpty() && lastKnownPickups.find { lastPickup -> lastPickup.id == it.id }?.driverId.isNullOrEmpty()
                }

                justAcceptedPickups.forEach { pickUp ->
                    Log.d("PickUpAcceptedService", "Processing pickup: ${pickUp.id} accepted by driver: ${pickUp.driverId}")
                    driversReference.child(pickUp.driverId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(driverSnapshot: DataSnapshot) {
                            val driver = driverSnapshot.getValue(Driver::class.java)
                            if (driver != null) {
                                Log.d("PickUpAcceptedService", "Driver found: ${driver.id}")
                                pickUpAcceptedNotificationService.showNotification(pickUp, driver)
                                //the above text shows a notification for a specific pickup and driver
                                //we can update the local pickups here
                            } else {
                                Log.e("PickUpAcceptedService", "Driver not found for ID: ${pickUp.driverId}")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("PickUpAcceptedService", "Failed to read driver data: ${error.message}")
                        }
                    })
                }

                lastKnownPickups = currentUserPickups
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PickUpAcceptedService", "Failed to read pickups data: ${error.message}")
            }
        }

        pickUpsReference.addValueEventListener(pickUpsListener)
        Log.d("PickUpAcceptedService", "ValueEventListener added to pickUpsReference.")
        return START_STICKY
    }
}
