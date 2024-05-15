package com.example.pickme

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.pickme.data.model.PickUp
import com.example.pickme.notifications.PickUpAcceptedService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PickUpAcceptedService: Service() {
    private lateinit var database: FirebaseDatabase
    private lateinit var pickUpsReference: DatabaseReference
    private lateinit var pickUpAcceptedService: PickUpAcceptedService
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        database = FirebaseDatabase.getInstance()
        pickUpsReference = database.getReference("PickUps")
        pickUpAcceptedService = PickUpAcceptedService(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE)
        val currentId = sharedPreferences.getString("lastUserId", "")
        var lastKnownPickups = listOf<PickUp>()
        val pickUpsListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pickups = snapshot.children.mapNotNull { it.getValue(PickUp::class.java) }
                val currentUserPickups = pickups.filter { it.id == currentId }
                val justAcceptedPickups = currentUserPickups.filter { it.driverId.isNotEmpty() && lastKnownPickups.find { lastPickup -> lastPickup.id == it.id }?.driverId.isNullOrEmpty() }
                if (justAcceptedPickups.isNotEmpty()) {
                    pickUpAcceptedService.showNotification()
                }
                lastKnownPickups = currentUserPickups
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        }
        pickUpsReference.addValueEventListener(pickUpsListener)
        return super.onStartCommand(intent, flags, startId)
    }
}