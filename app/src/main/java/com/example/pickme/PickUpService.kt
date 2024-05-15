package com.example.pickme

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.pickme.data.model.PickUp
import com.example.pickme.notifications.PickupsNotificationService
import com.example.pickme.view.ui.driver.HomeScreenVM
import com.google.firebase.database.*

class PickUpService : Service() {

    private lateinit var database: FirebaseDatabase
    private lateinit var pickUpsReference: DatabaseReference
    private lateinit var pickupsNotificationService: PickupsNotificationService
    private lateinit var homeScreenVM: HomeScreenVM
    private var previousFilteredPickups: List<PickUp>? = null


    override fun onCreate() {
        super.onCreate()
        database = FirebaseDatabase.getInstance()
        pickUpsReference = database.getReference("PickUps")
        pickupsNotificationService = PickupsNotificationService(this)
        homeScreenVM = HomeScreenVM(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val pickUpsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val pickups = dataSnapshot.children.mapNotNull { it.getValue(PickUp::class.java) }
                val filteredPickups = homeScreenVM.filterPickUps(this@PickUpService, pickups)
                if (previousFilteredPickups != null && filteredPickups != previousFilteredPickups) {
                    pickupsNotificationService.showNotification()
                }
                previousFilteredPickups = pickups
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Failed to read value
            }
        }
        pickUpsReference.addValueEventListener(pickUpsListener)
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }
}