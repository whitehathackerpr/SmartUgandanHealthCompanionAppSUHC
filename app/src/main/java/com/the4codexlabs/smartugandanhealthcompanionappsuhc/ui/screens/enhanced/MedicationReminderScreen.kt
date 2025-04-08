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
import java.text.SimpleDateFormat
import java.util.*
// Import the model classes instead of redefining them
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.Medication
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.Frequency
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.TimeOfDay

@Composable
fun MedicationReminderScreen(navController: NavController) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<MedicationFilter>(MedicationFilter.ALL) }
    
    val medications = remember {
        listOf(
            Medication(
                id = "1",
                name = "Amoxicillin",
                dosage = "500mg",
                frequency = Frequency.DAILY,
                times = listOf(TimeOfDay(8, 0), TimeOfDay(20, 0)),
                startDate = SimpleDateFormat("yyyy-MM-dd").parse("2023-09-01")!!,
                endDate = SimpleDateFormat("yyyy-MM-dd").parse("2023-09-10")!!,
                notes = "Take with food",
                isActive = true
            ),
            Medication(
                id = "2",
                name = "Metformin",
                dosage = "850mg",
                frequency = Frequency.DAILY,
                times = listOf(TimeOfDay(8, 0), TimeOfDay(14, 0)),
                startDate = SimpleDateFormat("yyyy-MM-dd").parse("2023-08-15")!!,
                endDate = null,
                notes = "Take with meals",
                isActive = true
            ),
            Medication(
                id = "3",
                name = "Lisinopril",
                dosage = "10mg",
                frequency = Frequency.DAILY,
                times = listOf(TimeOfDay(8, 0)),
                startDate = SimpleDateFormat("yyyy-MM-dd").parse("2023-07-20")!!,
                endDate = null,
                notes = "Take in the morning",
                isActive = true
            ),
            Medication(
                id = "4",
                name = "Vitamin D",
                dosage = "1000 IU",
                frequency = Frequency.DAILY,
                times = listOf(TimeOfDay(8, 0)),
                startDate = SimpleDateFormat("yyyy-MM-dd").parse("2023-06-10")!!,
                endDate = null,
                notes = "Take with breakfast",
                isActive = true
            )
        )
    }
    
    val filteredMedications = when (selectedFilter) {
        MedicationFilter.ALL -> medications
        MedicationFilter.ACTIVE -> medications.filter { it.isActive }
        MedicationFilter.COMPLETED -> medications.filter { !it.isActive }
        MedicationFilter.TODAY -> medications.filter { 
            it.isActive && it.times.any { time -> 
                val now = Calendar.getInstance()
                val currentHour = now.get(Calendar.HOUR_OF_DAY)
                val currentMinute = now.get(Calendar.MINUTE)
                
                // Check if any medication time is today
                time.hour > currentHour || (time.hour == currentHour && time.minute > currentMinute)
            }
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
                    
                    val todayMedications = medications.filter { 
                        it.isActive && it.times.any { time -> 
                            val now = Calendar.getInstance()
                            val currentHour = now.get(Calendar.HOUR_OF_DAY)
                            val currentMinute = now.get(Calendar.MINUTE)
                            
                            // Check if any medication time is today
                            time.hour > currentHour || (time.hour == currentHour && time.minute > currentMinute)
                        }
                    }
                    
                    if (todayMedications.isEmpty()) {
                        Text(
                            text = "No medications scheduled for today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        todayMedications.forEach { medication ->
                            TodayMedicationItem(medication = medication)
                            if (medication != todayMedications.last()) {
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
                MedicationFilter.values().forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter.name) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Medications List
            if (filteredMedications.isEmpty()) {
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
                    items(filteredMedications) { medication ->
                        MedicationCard(medication = medication)
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
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Medication")
            }
        }
    }
    
    // Add Medication Dialog
    if (showAddDialog) {
        AddMedicationDialog(
            onDismiss = { showAddDialog = false },
            onSave = { /* Handle saving new medication */ }
        )
    }
}

enum class MedicationFilter {
    ALL, ACTIVE, COMPLETED, TODAY
}

@Composable
fun TodayMedicationItem(medication: Medication) {
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
    }
}

@Composable
fun MedicationCard(medication: Medication) {
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
                    text = "Start: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(medication.startDate)}",
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
                TextButton(onClick = { /* Edit medication */ }) {
                    Text("Edit")
                }
                TextButton(onClick = { /* Toggle active status */ }) {
                    Text(if (medication.isActive) "Mark Complete" else "Reactivate")
                }
            }
        }
    }
}

@Composable
fun AddMedicationDialog(
    onDismiss: () -> Unit,
    onSave: (Medication) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf(Frequency.DAILY) }
    var notes by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(Date()) }
    var endDate by remember { mutableStateOf<Date?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Medication") },
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Frequency.values().forEach { freq ->
                        FilterChip(
                            selected = frequency == freq,
                            onClick = { frequency = freq },
                            label = { Text(freq.name) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Times per day")
                // Time picker would go here
                
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
                    // Create a new medication and save it
                    onSave(
                        Medication(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            dosage = dosage,
                            frequency = frequency,
                            times = listOf(TimeOfDay(8, 0)), // Default to morning
                            startDate = startDate,
                            endDate = endDate,
                            notes = notes
                        )
                    )
                    onDismiss()
                }
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