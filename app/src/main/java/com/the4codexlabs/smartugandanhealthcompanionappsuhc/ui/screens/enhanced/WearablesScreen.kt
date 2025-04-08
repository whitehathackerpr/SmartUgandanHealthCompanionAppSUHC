package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

data class WearableDevice(
    val id: String,
    val name: String,
    val type: DeviceType,
    val isConnected: Boolean,
    val batteryLevel: Int,
    val lastSynced: Date? = null,
    val dataPoints: List<HealthDataPoint> = emptyList()
)

enum class DeviceType {
    FITNESS_TRACKER, SMARTWATCH, BLOOD_PRESSURE_MONITOR, GLUCOSE_METER, HEART_RATE_MONITOR, 
    SLEEP_TRACKER, OXYGEN_SATURATION_MONITOR, WEIGHT_SCALE
}

data class HealthDataPoint(
    val id: String = "",
    val deviceId: String = "",
    val type: DataType,
    val value: Float,
    val unit: String,
    val timestamp: Date
)

enum class DataType {
    HEART_RATE, STEPS, CALORIES, DISTANCE, SLEEP, BLOOD_PRESSURE_SYSTOLIC, 
    BLOOD_PRESSURE_DIASTOLIC, BLOOD_OXYGEN, GLUCOSE, WEIGHT
}

@Composable
fun WearablesScreen(navController: NavController) {
    val viewModel: WearablesViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    var showAddDeviceDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf<String?>(null) }
    
    // Show error snackbar if there's an error
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            // Show snackbar
        }
    }

    BaseEnhancedScreen(
        navController = navController,
        title = "Wearables"
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Connected Devices Summary
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Connected Devices",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            DeviceSummaryItem(
                                count = uiState.devices.count { it.isConnected },
                                label = "Connected"
                            )
                            DeviceSummaryItem(
                                count = uiState.devices.count { !it.isConnected },
                                label = "Disconnected"
                            )
                            DeviceSummaryItem(
                                count = uiState.devices.sumOf { it.dataPoints.size },
                                label = "Data Points"
                            )
                        }
                    }
                }
                
                // Devices List
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.devices.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Watch,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No devices connected",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Connect your health devices to track your health data",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showAddDeviceDialog = true }
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Device")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.devices) { device ->
                            DeviceCard(
                                device = device,
                                onSync = { viewModel.syncDevice(device.id) },
                                onToggleConnection = { isConnected -> 
                                    viewModel.toggleDeviceConnection(device.id, isConnected)
                                },
                                onDelete = { showDeleteConfirmationDialog = device.id }
                            )
                        }
                        
                        item {
                            Button(
                                onClick = { showAddDeviceDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add New Device")
                            }
                        }
                    }
                }
            }
            
            // Loading overlay for syncing
            if (uiState.isSyncingDevice != null) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Syncing device...")
                        }
                    }
                }
            }
        }
        
        // Add Device Dialog
        if (showAddDeviceDialog) {
            AddDeviceDialog(
                onDismiss = { showAddDeviceDialog = false },
                onAdd = { name, type -> 
                    viewModel.addDevice(name, type)
                    showAddDeviceDialog = false
                }
            )
        }
        
        // Delete Confirmation Dialog
        showDeleteConfirmationDialog?.let { deviceId ->
            AlertDialog(
                onDismissRequest = { showDeleteConfirmationDialog = null },
                title = { Text("Delete Device") },
                text = { Text("Are you sure you want to delete this device? All associated data will be permanently removed.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteDevice(deviceId)
                            showDeleteConfirmationDialog = null
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmationDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // Error Snackbar
        uiState.error?.let { error ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(error)
            }
        }
    }
}

@Composable
fun DeviceSummaryItem(count: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun DeviceCard(
    device: WearableDevice,
    onSync: () -> Unit,
    onToggleConnection: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Device Icon
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                getDeviceIcon(device.type),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = device.type.name.replace("_", " "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Connection Status
                Switch(
                    checked = device.isConnected,
                    onCheckedChange = onToggleConnection
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Battery Level
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.BatteryFull,
                    contentDescription = null,
                    tint = getBatteryColor(device.batteryLevel)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${device.batteryLevel}%",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Last Synced
                device.lastSynced?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Last synced: ${formatTimestamp(it)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            // Data Points
            if (device.dataPoints.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Latest Data",
                    style = MaterialTheme.typography.titleSmall
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    device.dataPoints.take(3).forEach { dataPoint ->
                        DataPointChip(dataPoint = dataPoint)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
                TextButton(onClick = onSync) {
                    Text("Sync Now")
                }
            }
        }
    }
}

@Composable
fun DataPointChip(dataPoint: HealthDataPoint) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${dataPoint.value}${dataPoint.unit}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = dataPoint.type.name.replace("_", " "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun AddDeviceDialog(
    onDismiss: () -> Unit,
    onAdd: (String, DeviceType) -> Unit
) {
    var selectedType by remember { mutableStateOf(DeviceType.FITNESS_TRACKER) }
    var deviceName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Device") },
        text = {
            Column {
                OutlinedTextField(
                    value = deviceName,
                    onValueChange = { deviceName = it },
                    label = { Text("Device Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Device Type")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(
                    modifier = Modifier.height(200.dp)
                ) {
                    items(DeviceType.values()) { type ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedType == type,
                                onClick = { selectedType = type }
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(type.name.replace("_", " "))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (deviceName.isNotEmpty()) {
                        onAdd(deviceName, selectedType)
                    }
                },
                enabled = deviceName.isNotEmpty()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun getDeviceIcon(type: DeviceType) = when (type) {
    DeviceType.FITNESS_TRACKER -> Icons.Default.FitnessCenter
    DeviceType.SMARTWATCH -> Icons.Default.Watch
    DeviceType.BLOOD_PRESSURE_MONITOR -> Icons.Default.Favorite
    DeviceType.GLUCOSE_METER -> Icons.Default.Bloodtype
    DeviceType.HEART_RATE_MONITOR -> Icons.Default.Favorite
    DeviceType.SLEEP_TRACKER -> Icons.Default.Nightlight
    DeviceType.OXYGEN_SATURATION_MONITOR -> Icons.Default.Air
    DeviceType.WEIGHT_SCALE -> Icons.Default.MonitorWeight
}

fun getBatteryColor(level: Int): Color {
    return when {
        level > 70 -> Color(0xFF4CAF50) // Green
        level > 30 -> Color(0xFFFFC107) // Yellow
        else -> Color(0xFFF44336) // Red
    }
}

fun formatTimestamp(timestamp: Date): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp.time
    
    return when {
        diff < 60 * 1000 -> "Just now"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(timestamp)
    }
} 