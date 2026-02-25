package com.example.telegramstyle.newsapp.worker

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint

/**
 * Firebase Cloud Messaging service for handling push notifications.
 * Receives messages from Firebase and displays notifications to the user.
 */
@AndroidEntryPoint
class NewsFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // TODO: Handle incoming FCM messages
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Send token to server for push notification targeting
    }
}
