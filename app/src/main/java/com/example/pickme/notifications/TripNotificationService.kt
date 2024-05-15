package com.example.pickme.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.pickme.view.ui.driver.DriverView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TripNotificationService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    companion object {
        const val CHANNEL_ID = "Trip Notifications"
        const val CHANNEL_NAME = "Trip Notifications"
        const val CHANNEL_DESCRIPTION = "Trip Notifications"
    }

    private val tripsReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("Trips")
}