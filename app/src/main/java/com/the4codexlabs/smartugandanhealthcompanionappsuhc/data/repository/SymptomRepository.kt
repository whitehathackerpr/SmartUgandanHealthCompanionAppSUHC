package com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.Symptom
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*
import java.util.concurrent.TimeUnit

class SymptomRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
    
    private val symptomsCollection
        get() = firestore.collection("users").document(userId).collection("symptoms")
    
    // Get all symptoms
    fun getSymptoms(): Flow<List<Symptom>> = callbackFlow {
        val subscription = symptomsCollection
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val symptoms = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(Symptom::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(symptoms)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    // Get symptoms within a time range
    fun getSymptomsByTimeRange(period: String): Flow<List<Symptom>> = callbackFlow {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        
        // Determine start date based on period
        val startDate = when (period) {
            "week" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                calendar.time
            }
            "month" -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.time
            }
            "year" -> {
                calendar.add(Calendar.YEAR, -1)
                calendar.time
            }
            else -> null // "all" or invalid value
        }
        
        val query = if (startDate != null) {
            symptomsCollection
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .orderBy("date", Query.Direction.DESCENDING)
        } else {
            symptomsCollection
                .orderBy("date", Query.Direction.DESCENDING)
        }
        
        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle error
                return@addSnapshotListener
            }
            
            if (snapshot != null) {
                val symptoms = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Symptom::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(symptoms)
            }
        }
        
        awaitClose { subscription.remove() }
    }
    
    // Get symptom by ID
    suspend fun getSymptomById(symptomId: String): Symptom? {
        return try {
            val doc = symptomsCollection.document(symptomId).get().await()
            doc.toObject(Symptom::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }
    
    // Add or update symptom
    suspend fun saveSymptom(symptom: Symptom): String {
        val symptomId = symptom.id.takeIf { it.isNotEmpty() } ?: UUID.randomUUID().toString()
        val symptomWithId = symptom.copy(id = symptomId)
        
        symptomsCollection.document(symptomId).set(symptomWithId).await()
        return symptomId
    }
    
    // Delete symptom
    suspend fun deleteSymptom(symptomId: String) {
        symptomsCollection.document(symptomId).delete().await()
    }
    
    // Get symptom summary statistics
    suspend fun getSymptomSummary(period: String): Map<String, Any> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        
        // Determine start date based on period
        val startDate = when (period) {
            "week" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                calendar.time
            }
            "month" -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.time
            }
            "year" -> {
                calendar.add(Calendar.YEAR, -1)
                calendar.time
            }
            else -> {
                calendar.add(Calendar.YEAR, -100) // Effectively "all"
                calendar.time
            }
        }
        
        val query = symptomsCollection
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
            
        val snapshot = query.get().await()
        val symptoms = snapshot.documents.mapNotNull { doc ->
            try {
                doc.toObject(Symptom::class.java)
            } catch (e: Exception) {
                null
            }
        }
        
        // Calculate summary statistics
        val totalSymptoms = symptoms.size
        val averageSeverity = if (symptoms.isNotEmpty()) {
            symptoms.sumOf { it.severity.toDouble() } / totalSymptoms
        } else {
            0.0
        }
        
        val maxSeverity = symptoms.maxOfOrNull { it.severity } ?: 0
        
        // Count symptoms by name
        val symptomCounts = symptoms
            .groupBy { it.name }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .take(5)
            .toMap()
        
        // Find most frequently related symptoms
        val relatedSymptomCounts = symptoms
            .flatMap { it.relatedSymptoms ?: emptyList() }
            .groupBy { it }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .take(5)
            .toMap()
        
        return mapOf(
            "totalSymptoms" to totalSymptoms,
            "averageSeverity" to averageSeverity,
            "maxSeverity" to maxSeverity,
            "symptomCounts" to symptomCounts,
            "relatedSymptomCounts" to relatedSymptomCounts
        )
    }
} 