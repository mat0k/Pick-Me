package com.example.pickme

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.pickme.notifications.PickUpAcceptedService
import com.example.pickme.notifications.PickupsNotificationService
import com.example.pickme.notifications.TripNotificationService

import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val ONESIGNAL_APP_ID = "2e6ceeec-973c-494d-a6c2-619d833f261b"
class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()

        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)

        CoroutineScope(Dispatchers.IO).launch{
            OneSignal.Notifications.requestPermission(true)
        }

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            PickupsNotificationService.CHANNEL_ID,
            PickupsNotificationService.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = PickupsNotificationService.CHANNEL_DESCRIPTION
        }

        val channel2 = NotificationChannel(
            PickUpAcceptedService.CHANNEL_ID,
            PickUpAcceptedService.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = PickUpAcceptedService.CHANNEL_DESCRIPTION
        }

        val channel3 = NotificationChannel(
            TripNotificationService.CHANNEL_ID,
            TripNotificationService.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = TripNotificationService.CHANNEL_DESCRIPTION
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        notificationManager.createNotificationChannel(channel2)
        notificationManager.createNotificationChannel(channel3)
    }
}