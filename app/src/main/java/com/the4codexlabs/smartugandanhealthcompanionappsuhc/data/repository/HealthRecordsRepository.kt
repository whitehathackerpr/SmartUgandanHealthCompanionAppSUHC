package com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.HealthRecord
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.RecordType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*

class HealthRecordsRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
    
    private val recordsCollection
        get() = firestore.collection("users").document(userId).collection("records")
    
    // Get all health records
    fun getHealthRecords(): Flow<List<HealthRecord>> = callbackFlow {
        val subscription = recordsCollection
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val records = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(HealthRecord::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(records)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    // Get health records by type
    fun getHealthRecordsByType(type: RecordType): Flow<List<HealthRecord>> = callbackFlow {
        val subscription = recordsCollection
            .whereEqualTo("type", type.name)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val records = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(HealthRecord::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(records)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    // Search health records
    fun searchHealthRecords(query: String): Flow<List<HealthRecord>> = callbackFlow {
        val subscription = recordsCollection
            .orderBy("title")
            .startAt(query)
            .endAt(query + '\uf8ff')
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val records = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(HealthRecord::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(records)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    // Get record by ID
    suspend fun getHealthRecordById(recordId: String): HealthRecord? {
        return try {
            val doc = recordsCollection.document(recordId).get().await()
            doc.toObject(HealthRecord::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }
    
    // Add a new health record
    suspend fun addHealthRecord(
        title: String,
        type: RecordType,
        date: Date,
        description: String? = null,
        doctor: String? = null,
        location: String? = null,
        attachments: List<String> = emptyList()
    ) {
        val record = HealthRecord(
            id = "",  // Firestore will generate this
            title = title,
            type = type,
            date = date,
            description = description,
            doctor = doctor,
            location = location,
            attachments = attachments
        )
        
        recordsCollection.add(record).await()
    }
    
    // Update an existing health record
    suspend fun updateHealthRecord(record: HealthRecord) {
        if (record.id.isEmpty()) {
            throw IllegalArgumentException("Record ID cannot be empty for updates")
        }
        
        recordsCollection.document(record.id).set(record).await()
    }
    
    // Delete a health record
    suspend fun deleteHealthRecord(recordId: String) {
        recordsCollection.document(recordId).delete().await()
    }
} 