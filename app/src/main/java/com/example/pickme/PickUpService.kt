package com.example.pickme

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.pickme.data.model.PickUp
import com.example.pickme.notifications.PickupsNotificationService
import com.example.pickme.view.ui.driver.HomeScreenVM
import com.google.firebase.database.*
import com.google.maps.model.LatLng

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
                val pickups = dataSnapshot.children.mapNotNull { it.getValue(FirebasePickUp::class.java) }
                val filteredPickups = homeScreenVM.filterPickUps(this@PickUpService, pickups.map { it.toPickUp() })
                if (previousFilteredPickups != null && filteredPickups != previousFilteredPickups) {
                    pickupsNotificationService.showNotification()
                }
                previousFilteredPickups = pickups.map { it.toPickUp() }
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



data class LatLngWrapper(var latitude: Double = 0.0, var longitude: Double = 0.0) {
    constructor(latLng: com.google.android.gms.maps.model.LatLng) : this(latLng.latitude, latLng.longitude)
    fun toLatLng(): com.google.android.gms.maps.model.LatLng {
        return com.google.android.gms.maps.model.LatLng(latitude, longitude)
    }
}

data class FirebasePickUp(
    val id: String = "",
    val pickUpTitle: String = "",
    val targetTitle: String = "",
    val pickUpLatLng: LatLngWrapper = LatLngWrapper(),
    val targetLatLng: LatLngWrapper = LatLngWrapper(),
    val distance: Double = 0.0,
    val dateAndTime: String = "",
    val passengerId: String = "",
    val driverId : String = ""
) {
    fun toPickUp(): PickUp {
        return PickUp(
            id = this.id,
            pickUpTitle = this.pickUpTitle,
            targetTitle = this.targetTitle,
            pickUpLatLng = this.pickUpLatLng.toLatLng(),
            targetLatLng = this.targetLatLng.toLatLng(),
            distance = this.distance,
            dateAndTime = this.dateAndTime,
            passengerId = this.passengerId,
            driverId = this.driverId
        )
    }
}