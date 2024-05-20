package com.example.pickme.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.pickme.R
import com.example.pickme.view.ui.driver.DriverView
import java.util.Date

class TripNotificationService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    companion object {
        const val CHANNEL_ID = "Trip Notifications"
        const val CHANNEL_NAME = "Trip Notifications"
        const val CHANNEL_DESCRIPTION = "Trip Notifications"
    }

    fun showNotification(passengerName: String, time: Long) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("New Passenger Joined")
            .setContentText("$passengerName has joined your trip at ${Date(time)}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .build()

        notificationManager.notify(1, notification)
    }

}