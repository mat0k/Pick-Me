package com.example.pickme.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.pickme.data.model.PickUp
import com.example.pickme.view.ui.driver.DriverView
import okhttp3.internal.notify

class PickupsNotificationService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    companion object {
        const val CHANNEL_ID = "Pickup Requests"
        const val CHANNEL_NAME = "Pickup Requests"
        const val CHANNEL_DESCRIPTION = "Pickup Requests Notifications"
    }

    fun showNotification(newPickup: PickUp) {
        val activityIntent = Intent(context, DriverView::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("New Pickup Request")
            .setContentText("A new pickup request has been added")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(android.R.drawable.ic_menu_view, "View", pendingIntent)
            .build()
        notification.notify()
    }
}