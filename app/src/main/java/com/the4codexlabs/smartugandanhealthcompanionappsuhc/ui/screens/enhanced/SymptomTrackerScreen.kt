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
// Import the model class instead of redefining it
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.Symptom

@Composable
fun SymptomTrackerScreen(navController: NavController) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTimeRange by remember { mutableStateOf(TimeRange.WEEK) }
    
    val symptoms = remember {
        listOf(
            Symptom(
                "1",
                "Headache",
                7,
                SimpleDateFormat("yyyy-MM-dd").parse("2023-09-01")!!,
                "Pain in the front of the head, worsened by bright light",
                listOf("Fatigue", "Nausea")
            ),
            Symptom(
                "2",
                "Fever",
                8,
                SimpleDateFormat("yyyy-MM-dd").parse("2023-09-02")!!,
                "Temperature 38.5°C, accompanied by chills",
                listOf("Headache", "Fatigue")
            ),
            Symptom(
                "3",
                "Cough",
                5,
                SimpleDateFormat("yyyy-MM-dd").parse("2023-09-03")!!,
                "Dry cough, worse at night",
                listOf("Fatigue")
            ),
            Symptom(
                "4",
                "Fatigue",
                6,
                SimpleDateFormat("yyyy-MM-dd").parse("2023-09-04")!!,
                "Feeling tired throughout the day",
                listOf("Headache")
            ),
            Symptom(
                "5",
                "Nausea",
                4,
                SimpleDateFormat("yyyy-MM-dd").parse("2023-09-05")!!,
                "Mild nausea, no vomiting",
                listOf("Headache")
            )
        )
    }
    
    val filteredSymptoms = when (selectedTimeRange) {
        TimeRange.WEEK -> symptoms.filter { 
            val diff = System.currentTimeMillis() - it.date.time
            diff <= 7 * 24 * 60 * 60 * 1000 
        }
        TimeRange.MONTH -> symptoms.filter { 
            val diff = System.currentTimeMillis() - it.date.time
            diff <= 30 * 24 * 60 * 60 * 1000 
        }
        TimeRange.YEAR -> symptoms.filter { 
            val diff = System.currentTimeMillis() - it.date.time
            diff <= 365 * 24 * 60 * 60 * 1000 
        }
        TimeRange.ALL -> symptoms
    }

    BaseEnhancedScreen(
        navController = navController,
        title = "Symptom Tracker"
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Time Range Selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Time Range",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TimeRange.values().forEach { range ->
                            FilterChip(
                                selected = selectedTimeRange == range,
                                onClick = { selectedTimeRange = range },
                                label = { Text(range.name) }
                            )
                        }
                    }
                }
            }
            
            // Symptom Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
                        text = "Symptom Summary",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SymptomSummaryItem(
                            count = filteredSymptoms.size,
                            label = "Total Symptoms"
                        )
                        SymptomSummaryItem(
                            count = filteredSymptoms.maxOfOrNull { it.severity } ?: 0,
                            label = "Max Severity"
                        )
                        SymptomSummaryItem(
                            count = filteredSymptoms.map { it.name }.distinct().size,
                            label = "Unique Symptoms"
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Symptoms List
            if (filteredSymptoms.isEmpty()) {
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
                            Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No symptoms recorded",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add your first symptom to start tracking",
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
                    items(filteredSymptoms.sortedByDescending { it.date }) { symptom ->
                        SymptomCard(symptom = symptom)
                    }
                }
            }
        }
        
        // FAB for adding new symptom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Symptom")
            }
        }
    }
    
    // Add Symptom Dialog
    if (showAddDialog) {
        AddSymptomDialog(
            onDismiss = { showAddDialog = false },
            onSave = { /* Handle saving new symptom */ }
        )
    }
}

enum class TimeRange {
    WEEK, MONTH, YEAR, ALL
}

@Composable
fun SymptomSummaryItem(count: Int, label: String) {
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
fun SymptomCard(symptom: Symptom) {
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
                    text = symptom.name,
                    style = MaterialTheme.typography.titleMedium
                )
                
                // Severity Indicator
                SeverityIndicator(severity = symptom.severity)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(symptom.date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (symptom.notes?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = symptom.notes!!,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (symptom.relatedSymptoms?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Related Symptoms:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    symptom.relatedSymptoms?.forEach { relatedSymptom ->
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = relatedSymptom,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { /* View details */ }) {
                    Text("View Details")
                }
            }
        }
    }
}

@Composable
fun SeverityIndicator(severity: Int) {
    val color = when {
        severity <= 3 -> Color(0xFF4CAF50) // Green
        severity <= 6 -> Color(0xFFFFC107) // Yellow
        severity <= 8 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }
    
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "Severity: $severity",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}

@Composable
fun AddSymptomDialog(
    onDismiss: () -> Unit,
    onSave: (Symptom) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf(5) }
    var notes by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Symptom") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Symptom Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Severity (1-10)")
                Slider(
                    value = severity.toFloat(),
                    onValueChange = { severity = it.toInt() },
                    valueRange = 1f..10f,
                    steps = 8
                )
                Text(
                    text = "Current: $severity",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End)
                )
                
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
                    // Create a new symptom and save it
                    onSave(
                        Symptom(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            severity = severity,
                            date = Date(),
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