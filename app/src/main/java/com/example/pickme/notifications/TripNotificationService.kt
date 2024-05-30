package com.example.pickme.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.pickme.data.repository.TripsRepository
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

    suspend fun showNotification(tripId: String) {
        val tripTitle = TripsRepository().getTripTitle(tripId)
        tripTitle?.let {
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("New passenger joined your trip")
                .setContentText("Trip: $tripTitle")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0, Intent(context, DriverView::class.java), PendingIntent.FLAG_UPDATE_CURRENT))
                .build()
            notificationManager.notify(Date().time.toInt(), notification)
        }
    }


}