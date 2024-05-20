package com.example.pickme.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.pickme.FirebasePickUp
import com.example.pickme.data.model.Driver
import com.example.pickme.view.ui.passenger.PassengerView

class PickUpAcceptedNotificationService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "Pickup Accepted"
        const val CHANNEL_NAME = "Pickup Accepted"
        const val CHANNEL_DESCRIPTION = "Pickup Accepted Notifications"
    }

    fun showNotification(pickUp: FirebasePickUp, driver: Driver) {
        val activityIntent = Intent(context, PassengerView::class.java).apply {
            putExtra("PICKUP_ID", pickUp.id)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationText = "Driver ${driver.name} ${driver.surname} accepted your pickup request"
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Pickup Accepted")
            .setContentText(notificationText)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(1, notification)
    }
}
