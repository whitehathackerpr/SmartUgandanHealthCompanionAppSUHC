package com.the4codexlabs.smartugandanhealthcompanionappsuhc.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.MainActivity
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SUHCFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "SUHC_NOTIFICATIONS"
        private const val CHANNEL_NAME = "SUHC Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications from Smart Ugandan Health Companion"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Send this token to your server
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent for notification tap action
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Add data to handle different notification types
            message.data["type"]?.let { type ->
                putExtra("notification_type", type)
                when (type) {
                    "group_message" -> {
                        message.data["group_id"]?.let { groupId ->
                            putExtra("group_id", groupId)
                        }
                    }
                    "event" -> {
                        message.data["event_id"]?.let { eventId ->
                            putExtra("event_id", eventId)
                        }
                    }
                    "medication" -> {
                        message.data["medication_id"]?.let { medicationId ->
                            putExtra("medication_id", medicationId)
                        }
                    }
                }
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Determine icon based on notification type
        val iconResId = when (message.data["type"]) {
            "group_message" -> R.drawable.ic_notification_group
            "event" -> R.drawable.ic_notification_event
            "medication" -> R.drawable.ic_notification_medication
            else -> R.drawable.ic_notification
        }

        // Build notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.notification?.title ?: "New Notification")
            .setContentText(message.notification?.body)
            .setSmallIcon(iconResId)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Show notification
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
} 