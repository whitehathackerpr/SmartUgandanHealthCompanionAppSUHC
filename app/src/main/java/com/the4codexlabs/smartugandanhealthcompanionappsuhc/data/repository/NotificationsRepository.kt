package com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.Notification
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.NotificationType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

class NotificationsRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private val notificationsCollection 
        get() = firestore.collection("users")
            .document(getCurrentUserId())
            .collection("notifications")
    
    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: "guest_user"
    }
    
    fun getNotifications(): Flow<List<Notification>> = callbackFlow {
        val listenerRegistration = notificationsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val notifications = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val id = doc.id
                        val title = doc.getString("title") ?: ""
                        val message = doc.getString("message") ?: ""
                        val timestamp = doc.getDate("timestamp") ?: Date()
                        val typeString = doc.getString("type") ?: "GENERAL"
                        val isRead = doc.getBoolean("isRead") ?: false
                        
                        val type = try {
                            NotificationType.valueOf(typeString)
                        } catch (e: IllegalArgumentException) {
                            NotificationType.GENERAL
                        }
                        
                        Notification(
                            id = id,
                            title = title,
                            message = message,
                            timestamp = timestamp,
                            type = type,
                            isRead = isRead
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                trySend(notifications)
            }
        
        awaitClose { listenerRegistration.remove() }
    }
    
    suspend fun markAsRead(notificationId: String) {
        try {
            notificationsCollection.document(notificationId)
                .update("isRead", true)
                .await()
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    suspend fun markAllAsRead() {
        try {
            val batch = firestore.batch()
            val unreadNotifications = notificationsCollection
                .whereEqualTo("isRead", false)
                .get()
                .await()
            
            for (doc in unreadNotifications.documents) {
                batch.update(doc.reference, "isRead", true)
            }
            
            batch.commit().await()
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    suspend fun clearNotification(notificationId: String) {
        try {
            notificationsCollection.document(notificationId)
                .delete()
                .await()
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    suspend fun addNotification(
        title: String,
        message: String,
        type: NotificationType = NotificationType.GENERAL
    ) {
        try {
            val notification = hashMapOf(
                "title" to title,
                "message" to message,
                "timestamp" to Date(),
                "type" to type.name,
                "isRead" to false
            )
            
            notificationsCollection.add(notification).await()
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    // Create some sample notifications for testing
    suspend fun createSampleNotifications() {
        try {
            // Check if user already has notifications
            val existing = notificationsCollection.limit(1).get().await()
            if (!existing.isEmpty) {
                return // Don't add samples if user already has notifications
            }
            
            val notifications = listOf(
                hashMapOf(
                    "title" to "Welcome to SUHC",
                    "message" to "Thank you for joining Smart Ugandan Health Companion. Set up your profile to get personalized health recommendations.",
                    "timestamp" to Date(System.currentTimeMillis() - 1000 * 60),
                    "type" to NotificationType.SYSTEM.name,
                    "isRead" to false
                ),
                hashMapOf(
                    "title" to "Complete Your Health Profile",
                    "message" to "Enter your health information to get better recommendations and tracking.",
                    "timestamp" to Date(System.currentTimeMillis() - 1000 * 60 * 30),
                    "type" to NotificationType.GENERAL.name,
                    "isRead" to false
                ),
                hashMapOf(
                    "title" to "Health Tip of the Day",
                    "message" to "Staying hydrated is important for overall health. Aim to drink 8 glasses of water daily.",
                    "timestamp" to Date(System.currentTimeMillis() - 1000 * 60 * 60 * 3),
                    "type" to NotificationType.HEALTH_ALERT.name,
                    "isRead" to false
                )
            )
            
            val batch = firestore.batch()
            notifications.forEach { notification ->
                val docRef = notificationsCollection.document()
                batch.set(docRef, notification)
            }
            
            batch.commit().await()
        } catch (e: Exception) {
            // Handle error
        }
    }
} 