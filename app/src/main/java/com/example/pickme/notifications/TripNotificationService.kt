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

    fun showNotification() {
        val activityIntent = Intent(context, DriverView::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("New Passenger Joined")
            .setContentText("A new passenger has joined your trip")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(android.R.drawable.ic_menu_view, "View", pendingIntent)
            .build()
        notificationManager.notify(1, notification)
    }

}