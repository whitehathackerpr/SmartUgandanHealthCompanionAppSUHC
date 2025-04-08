package com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.Frequency
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.Medication
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.TimeOfDay
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*

class MedicationRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
    
    private val medicationsCollection
        get() = firestore.collection("users").document(userId).collection("medications")
    
    // Get all medications
    fun getMedications(): Flow<List<Medication>> = callbackFlow {
        val subscription = medicationsCollection
            .orderBy("name")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val medications = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(Medication::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(medications)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    // Get medications for today
    fun getTodayMedications(): Flow<List<Medication>> = callbackFlow {
        val subscription = medicationsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val medications = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(Medication::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    // Filter medications that are due today
                    val today = Calendar.getInstance()
                    val todayMedications = medications.filter { medication ->
                        val startDate = medication.startDate?.let { Calendar.getInstance().apply { time = it } }
                        val endDate = medication.endDate?.let { Calendar.getInstance().apply { time = it } }
                        
                        // Check if today is within the medication date range
                        val isWithinDateRange = (startDate == null || !today.before(startDate)) &&
                                (endDate == null || !today.after(endDate))
                        
                        // Check if medication is due today based on frequency
                        val isDueToday = when (medication.frequency) {
                            Frequency.DAILY -> true
                            Frequency.WEEKLY -> {
                                val dayOfWeek = today.get(Calendar.DAY_OF_WEEK)
                                medication.daysOfWeek?.contains(dayOfWeek) ?: false
                            }
                            Frequency.MONTHLY -> {
                                val dayOfMonth = today.get(Calendar.DAY_OF_MONTH)
                                medication.daysOfMonth?.contains(dayOfMonth) ?: false
                            }
                            Frequency.AS_NEEDED -> false
                            null -> false
                        }
                        
                        isWithinDateRange && isDueToday
                    }
                    
                    trySend(todayMedications)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    // Add or update medication
    suspend fun saveMedication(medication: Medication): String {
        val medicationId = medication.id.takeIf { it.isNotEmpty() } ?: UUID.randomUUID().toString()
        val medicationWithId = medication.copy(id = medicationId)
        
        medicationsCollection.document(medicationId).set(medicationWithId).await()
        return medicationId
    }
    
    // Delete medication
    suspend fun deleteMedication(medicationId: String) {
        medicationsCollection.document(medicationId).delete().await()
    }
    
    // Toggle medication taken status
    suspend fun toggleMedicationStatus(medicationId: String, timeOfDay: TimeOfDay, isTaken: Boolean) {
        val medication = medicationsCollection.document(medicationId).get().await()
            .toObject(Medication::class.java) ?: return
        
        val updatedTakenStatus = medication.takenStatus.toMutableMap()
        val today = Calendar.getInstance().apply { 
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        
        val todayKey = today.time.toString()
        val todayStatus = updatedTakenStatus[todayKey]?.toMutableMap() ?: mutableMapOf()
        todayStatus[timeOfDay.name] = isTaken
        updatedTakenStatus[todayKey] = todayStatus
        
        medicationsCollection.document(medicationId).update("takenStatus", updatedTakenStatus).await()
    }
    
    // Get medications by status (taken, not taken, all)
    fun getMedicationsByStatus(status: String): Flow<List<Medication>> = callbackFlow {
        val subscription = medicationsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val medications = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(Medication::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    // Get medications due today
                    val today = Calendar.getInstance()
                    val todayMedications = medications.filter { medication ->
                        val startDate = medication.startDate?.let { Calendar.getInstance().apply { time = it } }
                        val endDate = medication.endDate?.let { Calendar.getInstance().apply { time = it } }
                        
                        // Check if today is within the medication date range
                        val isWithinDateRange = (startDate == null || !today.before(startDate)) &&
                                (endDate == null || !today.after(endDate))
                        
                        // Check if medication is due today based on frequency
                        val isDueToday = when (medication.frequency) {
                            Frequency.DAILY -> true
                            Frequency.WEEKLY -> {
                                val dayOfWeek = today.get(Calendar.DAY_OF_WEEK)
                                medication.daysOfWeek?.contains(dayOfWeek) ?: false
                            }
                            Frequency.MONTHLY -> {
                                val dayOfMonth = today.get(Calendar.DAY_OF_MONTH)
                                medication.daysOfMonth?.contains(dayOfMonth) ?: false
                            }
                            Frequency.AS_NEEDED -> false
                            null -> false
                        }
                        
                        isWithinDateRange && isDueToday
                    }
                    
                    // Filter by status
                    val todayKey = today.apply { 
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time.time.toString()
                    
                    val filteredMedications = when (status) {
                        "taken" -> todayMedications.filter { medication ->
                            val todayStatus = medication.takenStatus[todayKey]
                            todayStatus != null && todayStatus.values.any { it }
                        }
                        "not_taken" -> todayMedications.filter { medication ->
                            val todayStatus = medication.takenStatus[todayKey]
                            todayStatus == null || todayStatus.values.any { !it }
                        }
                        else -> todayMedications
                    }
                    
                    trySend(filteredMedications)
                }
            }
        
        awaitClose { subscription.remove() }
    }
} 