package com.the4codexlabs.smartugandanhealthcompanionappsuhc.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.MainActivity
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.R
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.SUHCApplication.Companion.CHANNEL_GENERAL
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.SUHCApplication.Companion.CHANNEL_REMINDERS
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.SUHCApplication.Companion.CHANNEL_SOS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Service to handle Firebase Cloud Messaging (FCM) messages.
 * This service processes incoming push notifications and displays them to the user.
 */
class SUHCFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when a message is received.
     * @param remoteMessage The message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            
            // Get notification type from data
            val notificationType = remoteMessage.data["type"] ?: "general"
            
            // Handle the received data based on type
            handleNotification(
                remoteMessage.data["title"] ?: "Health Companion",
                remoteMessage.data["message"] ?: "You have a new notification",
                notificationType
            )
        }

        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            
            // Handle the notification payload
            handleNotification(
                it.title ?: "Health Companion",
                it.body ?: "You have a new notification",
                "general"
            )
        }
    }

    /**
     * Called when a new token is generated.
     * @param token The new FCM registration token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        
        // Send the new token to your server for targeting this device
        sendRegistrationToServer(token)
    }

    /**
     * Handle showing the notification to the user.
     * @param title The notification title.
     * @param messageBody The notification message body.
     * @param type The type of notification (sos, reminder, or general).
     */
    private fun handleNotification(title: String, messageBody: String, type: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("notification_type", type)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = when (type) {
            "sos" -> CHANNEL_SOS
            "reminder" -> CHANNEL_REMINDERS
            else -> CHANNEL_GENERAL
        }
        
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            
        // Set high priority for SOS notifications
        if (type == "sos") {
            notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(getNotificationId(type), notificationBuilder.build())
    }

    /**
     * Generate a notification ID based on the notification type.
     * This ensures different types of notifications don't override each other.
     */
    private fun getNotificationId(type: String): Int {
        return when (type) {
            "sos" -> SOS_NOTIFICATION_ID
            "reminder" -> REMINDER_NOTIFICATION_ID
            else -> (System.currentTimeMillis() % 10000).toInt()
        }
    }

    /**
     * Send the FCM registration token to your server.
     * @param token The FCM registration token.
     */
    private fun sendRegistrationToServer(token: String) {
        // Save token to Firestore for the current user
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        
        if (currentUser != null) {
            val userId = currentUser.uid
            val tokenData = hashMapOf(
                "token" to token,
                "platform" to "android",
                "updatedAt" to com.google.firebase.Timestamp.now()
            )
            
            // Use coroutine to perform the Firestore operation
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                try {
                    // Save token to user's document
                    com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(userId)
                        .collection("fcm_tokens")
                        .document(token)
                        .set(tokenData)
                        .addOnSuccessListener {
                            Log.d(TAG, "FCM token successfully saved to Firestore")
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error saving FCM token to Firestore", e)
                        }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in sendRegistrationToServer", e)
                }
            }
        } else {
            Log.d(TAG, "User not logged in, FCM token not saved: $token")
        }
    }

    companion object {
        private const val TAG = "SUHCFirebaseMsgService"
        private const val SOS_NOTIFICATION_ID = 1001
        private const val REMINDER_NOTIFICATION_ID = 1002
    }
}