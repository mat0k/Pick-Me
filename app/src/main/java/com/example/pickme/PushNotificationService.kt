package com.example.pickme

import com.example.pickme.data.repository.AuthRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationService: FirebaseMessagingService()  {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val authRepository = AuthRepository()
        authRepository.updateToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }
}