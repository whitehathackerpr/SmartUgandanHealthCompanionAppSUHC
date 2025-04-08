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
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*
// Import the model classes instead of redefining them
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.Medication
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.Frequency
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.TimeOfDay

@Composable
fun MedicationReminderScreen(
    navController: NavController,
    viewModel: MedicationReminderViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showErrorSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            showErrorSnackbar = true
        }
    }

    BaseEnhancedScreen(
        navController = navController,
        title = "Medication Reminder"
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Today's Medications Card
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
                        text = "Today's Medications",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else if (uiState.todayMedications.isEmpty()) {
                        Text(
                            text = "No medications scheduled for today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        uiState.todayMedications.forEach { medication ->
                            TodayMedicationItem(
                                medication = medication,
                                onToggleStatus = { timeOfDay ->
                                    viewModel.toggleMedicationStatus(medication, timeOfDay)
                                }
                            )
                            if (medication != uiState.todayMedications.last()) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                }
            }
            
            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedStatusFilter == "all",
                    onClick = { viewModel.setStatusFilter("all") },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = uiState.selectedStatusFilter == "taken",
                    onClick = { viewModel.setStatusFilter("taken") },
                    label = { Text("Taken") }
                )
                FilterChip(
                    selected = uiState.selectedStatusFilter == "not_taken",
                    onClick = { viewModel.setStatusFilter("not_taken") },
                    label = { Text("Not Taken") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Medications List
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.medications.isEmpty()) {
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
                            Icons.Default.Medication,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No medications found",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add your first medication to start tracking",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.medications) { medication ->
                        MedicationCard(
                            medication = medication,
                            onEdit = { viewModel.showAddMedicationDialog(true, medication) },
                            onToggleStatus = { viewModel.toggleMedicationStatus(medication, it) }
                        )
                    }
                }
            }
        }
        
        // FAB for adding new medication
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { viewModel.showAddMedicationDialog(true) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Medication")
            }
        }
    }
    
    // Add Medication Dialog
    if (uiState.showAddMedicationDialog) {
        AddMedicationDialog(
            onDismiss = { viewModel.showAddMedicationDialog(false) },
            onSave = { name, dosage, frequency, times, startDate, endDate, notes, daysOfWeek, daysOfMonth ->
                viewModel.saveMedication(
                    name = name,
                    dosage = dosage,
                    frequency = frequency,
                    times = times,
                    startDate = startDate,
                    endDate = endDate,
                    notes = notes,
                    daysOfWeek = daysOfWeek,
                    daysOfMonth = daysOfMonth
                )
            },
            medicationToEdit = uiState.medicationToEdit
        )
    }
    
    // Error Snackbar
    if (showErrorSnackbar && uiState.error != null) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { 
                    showErrorSnackbar = false
                    viewModel.clearError()
                }) {
                    Text("Dismiss")
                }
            }
        ) {
            Text(uiState.error)
        }
    }
}

@Composable
fun TodayMedicationItem(
    medication: Medication,
    onToggleStatus: (TimeOfDay) -> Unit
) {
    val nextDose = medication.times.minByOrNull { time ->
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)
        
        if (time.hour > currentHour || (time.hour == currentHour && time.minute > currentMinute)) {
            // Today
            time.hour * 60 + time.minute - (currentHour * 60 + currentMinute)
        } else {
            // Tomorrow
            24 * 60 - (currentHour * 60 + currentMinute) + time.hour * 60 + time.minute
        }
    }
    
    val timeString = nextDose?.let {
        String.format("%02d:%02d", it.hour, it.minute)
    } ?: "N/A"
    
    val today = Calendar.getInstance().apply { 
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time.time.toString()
    
    val todayStatus = medication.takenStatus[today]
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = medication.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = medication.dosage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
        
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "Next dose:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = timeString,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Add checkboxes for each time of day
        medication.times.forEach { timeOfDay ->
            val isTaken = todayStatus?.get(timeOfDay.name) ?: false
            
            Checkbox(
                checked = isTaken,
                onCheckedChange = { onToggleStatus(timeOfDay) },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    uncheckedColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            )
        }
    }
}

@Composable
fun MedicationCard(
    medication: Medication,
    onEdit: () -> Unit,
    onToggleStatus: (TimeOfDay) -> Unit
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
                Text(
                    text = medication.name,
                    style = MaterialTheme.typography.titleMedium
                )
                
                // Status Badge
                Surface(
                    color = if (medication.isActive) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = if (medication.isActive) "Active" else "Completed",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (medication.isActive) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Dosage: ${medication.dosage}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Frequency: ${medication.frequency?.name ?: "Unknown"}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Times: ${medication.times.joinToString(", ") { String.format("%02d:%02d", it.hour, it.minute) }}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (medication.notes?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Notes: ${medication.notes}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Start: ${medication.startDate?.let { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it) } ?: "Not set"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                medication.endDate?.let {
                    Text(
                        text = "End: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text("Edit")
                }
                TextButton(onClick = { /* Toggle active status */ }) {
                    Text(if (medication.isActive) "Mark Complete" else "Reactivate")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Frequency, List<TimeOfDay>, Date?, Date?, String?, List<Int>?, List<Int>?) -> Unit,
    medicationToEdit: Medication? = null
) {
    var name by remember { mutableStateOf(medicationToEdit?.name ?: "") }
    var dosage by remember { mutableStateOf(medicationToEdit?.dosage ?: "") }
    var frequency by remember { mutableStateOf(medicationToEdit?.frequency ?: Frequency.DAILY) }
    var notes by remember { mutableStateOf(medicationToEdit?.notes ?: "") }
    var startDate by remember { mutableStateOf(medicationToEdit?.startDate ?: Date()) }
    var endDate by remember { mutableStateOf<Date?>(medicationToEdit?.endDate) }
    var times by remember { mutableStateOf(medicationToEdit?.times ?: listOf(TimeOfDay(8, 0))) }
    var daysOfWeek by remember { mutableStateOf(medicationToEdit?.daysOfWeek ?: listOf()) }
    var daysOfMonth by remember { mutableStateOf(medicationToEdit?.daysOfMonth ?: listOf()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (medicationToEdit != null) "Edit Medication" else "Add Medication") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medication Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Frequency")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                var expandedFrequency by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expandedFrequency,
                    onExpandedChange = { expandedFrequency = it }
                ) {
                    OutlinedTextField(
                        value = frequency.name,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Frequency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFrequency) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedFrequency,
                        onDismissRequest = { expandedFrequency = false }
                    ) {
                        Frequency.values().forEach { freq ->
                            DropdownMenuItem(
                                text = { Text(text = freq.name) },
                                onClick = {
                                    frequency = freq
                                    expandedFrequency = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Time selection
                Text("Times")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Display current times
                times.forEachIndexed { index, time ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${index + 1}. ${String.format("%02d:%02d", time.hour, time.minute)}")
                        
                        IconButton(onClick = {
                            times = times.filterIndexed { i, _ -> i != index }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove time")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Add time button
                Button(
                    onClick = {
                        // Add a new time (default to next hour)
                        val now = Calendar.getInstance()
                        val nextHour = (now.get(Calendar.HOUR_OF_DAY) + 1) % 24
                        times = times + TimeOfDay(nextHour, 0)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Time")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        name,
                        dosage,
                        frequency,
                        times,
                        startDate,
                        endDate,
                        notes,
                        daysOfWeek,
                        daysOfMonth
                    )
                },
                enabled = name.isNotBlank() && dosage.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return formatter.format(date)
} 