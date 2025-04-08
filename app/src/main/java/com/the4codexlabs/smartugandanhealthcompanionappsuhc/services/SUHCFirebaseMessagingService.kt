package com.the4codexlabs.smartugandanhealthcompanionappsuhc.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.MainActivity
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.R
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.SUHCApplication.Companion.CHANNEL_GENERAL
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.SUHCApplication.Companion.CHANNEL_REMINDERS
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.SUHCApplication.Companion.CHANNEL_SOS
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository.NotificationsRepository
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.NotificationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

/**
 * Service to handle Firebase Cloud Messaging (FCM) messages.
 * This service processes incoming push notifications and displays them to the user.
 */
class SUHCFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationIdSource = AtomicInteger(0)
    private val notificationsRepository = NotificationsRepository()

    /**
     * Called when a message is received.
     * @param remoteMessage The message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // Check if message contains a notification payload
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "SUHC Notification"
            val body = notification.body ?: "You have a new notification"
            
            // Show notification
            sendNotification(title, body)
            
            // Save to Firestore if user is logged in
            saveNotificationToFirestore(title, body, remoteMessage.data)
        }
    }

    /**
     * Called when a new token is generated.
     * @param token The new FCM registration token.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        
        // If you want to send messages to this application instance or
        // manage this app's subscriptions on the server side, send the
        // FCM registration token to your app server.
        // For now, we'll just log it
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        // Implement token sending to your server if needed
        // For now, just log it or handle locally
    }

    private fun sendNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create the notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "SUHC Notification Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Smart Ugandan Health Companion notifications"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        notificationManager.notify(notificationIdSource.incrementAndGet(), notificationBuilder.build())
    }
    
    private fun saveNotificationToFirestore(title: String, body: String, data: Map<String, String>) {
        // Only save if user is logged in
        if (FirebaseAuth.getInstance().currentUser != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Parse notification type from data
                    val typeString = data["type"] ?: "GENERAL"
                    val type = try {
                        NotificationType.valueOf(typeString)
                    } catch (e: IllegalArgumentException) {
                        NotificationType.GENERAL
                    }
                    
                    // Save notification to Firestore
                    notificationsRepository.addNotification(
                        title = title,
                        message = body,
                        type = type
                    )
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }

    companion object {
        private const val TAG = "SUHCFirebaseMsgService"
        private const val SOS_NOTIFICATION_ID = 1001
        private const val REMINDER_NOTIFICATION_ID = 1002
    }
}