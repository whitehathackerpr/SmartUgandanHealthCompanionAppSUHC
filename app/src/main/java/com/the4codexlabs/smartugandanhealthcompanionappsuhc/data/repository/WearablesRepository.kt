package com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.DeviceType
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.HealthDataPoint
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.WearableDevice
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*

class WearablesRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
    
    private val devicesCollection
        get() = firestore.collection("users").document(userId).collection("devices")
    
    private val dataPointsCollection
        get() = firestore.collection("users").document(userId).collection("dataPoints")
    
    // Get all devices for the current user
    fun getDevices(): Flow<List<WearableDevice>> = callbackFlow {
        val subscription = devicesCollection
            .orderBy("name")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val devices = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(WearableDevice::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(devices)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    // Get data points for a specific device
    fun getDataPoints(deviceId: String): Flow<List<HealthDataPoint>> = callbackFlow {
        val subscription = dataPointsCollection
            .whereEqualTo("deviceId", deviceId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val dataPoints = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(HealthDataPoint::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(dataPoints)
                }
            }
        
        awaitClose { subscription.remove() }
    }
    
    // Add a new device
    suspend fun addDevice(device: WearableDevice): String {
        val deviceId = UUID.randomUUID().toString()
        val deviceWithId = device.copy(id = deviceId)
        
        devicesCollection.document(deviceId).set(deviceWithId).await()
        return deviceId
    }
    
    // Update device connection status
    suspend fun updateDeviceConnectionStatus(deviceId: String, isConnected: Boolean) {
        devicesCollection.document(deviceId).update("isConnected", isConnected).await()
    }
    
    // Update device battery level
    suspend fun updateDeviceBatteryLevel(deviceId: String, batteryLevel: Int) {
        devicesCollection.document(deviceId).update("batteryLevel", batteryLevel).await()
    }
    
    // Add a new data point
    suspend fun addDataPoint(deviceId: String, dataPoint: HealthDataPoint) {
        val dataPointId = UUID.randomUUID().toString()
        val dataPointWithId = dataPoint.copy(id = dataPointId, deviceId = deviceId)
        
        dataPointsCollection.document(dataPointId).set(dataPointWithId).await()
        
        // Update last synced time for the device
        devicesCollection.document(deviceId).update("lastSynced", Date()).await()
    }
    
    // Sync device data (simulated)
    suspend fun syncDevice(deviceId: String) {
        // In a real app, this would connect to the device's API and fetch new data
        // For now, we'll simulate syncing by updating the last synced time
        devicesCollection.document(deviceId).update("lastSynced", Date()).await()
        
        // Simulate adding some new data points
        val device = devicesCollection.document(deviceId).get().await()
            .toObject(WearableDevice::class.java) ?: return
        
        when (device.type) {
            DeviceType.FITNESS_TRACKER -> {
                addDataPoint(deviceId, HealthDataPoint(
                    id = UUID.randomUUID().toString(),
                    deviceId = deviceId,
                    type = com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.DataType.HEART_RATE,
                    value = (60..100).random().toFloat(),
                    unit = "bpm",
                    timestamp = Date()
                ))
                addDataPoint(deviceId, HealthDataPoint(
                    id = UUID.randomUUID().toString(),
                    deviceId = deviceId,
                    type = com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.DataType.STEPS,
                    value = (5000..15000).random().toFloat(),
                    unit = "steps",
                    timestamp = Date()
                ))
            }
            DeviceType.BLOOD_PRESSURE_MONITOR -> {
                addDataPoint(deviceId, HealthDataPoint(
                    id = UUID.randomUUID().toString(),
                    deviceId = deviceId,
                    type = com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.DataType.BLOOD_PRESSURE_SYSTOLIC,
                    value = (110..140).random().toFloat(),
                    unit = "mmHg",
                    timestamp = Date()
                ))
                addDataPoint(deviceId, HealthDataPoint(
                    id = UUID.randomUUID().toString(),
                    deviceId = deviceId,
                    type = com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.DataType.BLOOD_PRESSURE_DIASTOLIC,
                    value = (70..90).random().toFloat(),
                    unit = "mmHg",
                    timestamp = Date()
                ))
            }
            else -> {
                // Add generic data point for other device types
                addDataPoint(deviceId, HealthDataPoint(
                    id = UUID.randomUUID().toString(),
                    deviceId = deviceId,
                    type = com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.DataType.HEART_RATE,
                    value = (60..100).random().toFloat(),
                    unit = "bpm",
                    timestamp = Date()
                ))
            }
        }
    }
    
    // Delete a device
    suspend fun deleteDevice(deviceId: String) {
        // Delete all data points for this device
        val dataPoints = dataPointsCollection.whereEqualTo("deviceId", deviceId).get().await()
        dataPoints.documents.forEach { doc ->
            dataPointsCollection.document(doc.id).delete().await()
        }
        
        // Delete the device
        devicesCollection.document(deviceId).delete().await()
    }
} 