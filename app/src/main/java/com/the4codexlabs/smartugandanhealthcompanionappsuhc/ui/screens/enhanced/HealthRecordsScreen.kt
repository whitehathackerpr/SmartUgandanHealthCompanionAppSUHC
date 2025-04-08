package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*
// Import the model classes instead of redefining them
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.HealthRecord
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.RecordType

@Composable
fun HealthRecordsScreen(navController: NavController) {
    var selectedFilter by remember { mutableStateOf<RecordType?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    
    val records = remember {
        listOf(
            HealthRecord(
                id = "1",
                title = "COVID-19 Vaccination",
                type = RecordType.VACCINATION,
                date = SimpleDateFormat("yyyy-MM-dd").parse("2023-05-15")!!,
                description = "First dose of COVID-19 vaccine administered at Mulago Hospital."
            ),
            HealthRecord(
                id = "2",
                title = "Blood Test Results",
                type = RecordType.LAB_RESULT,
                date = SimpleDateFormat("yyyy-MM-dd").parse("2023-06-20")!!,
                description = "Complete blood count and lipid panel. All values within normal range."
            ),
            HealthRecord(
                id = "3",
                title = "Malaria Treatment",
                type = RecordType.PRESCRIPTION,
                date = SimpleDateFormat("yyyy-MM-dd").parse("2023-07-10")!!,
                description = "Prescribed Coartem for malaria treatment. Completed full course."
            ),
            HealthRecord(
                id = "4",
                title = "Annual Check-up",
                type = RecordType.APPOINTMENT,
                date = SimpleDateFormat("yyyy-MM-dd").parse("2023-08-05")!!,
                description = "Annual physical examination with Dr. Namukasa. All vitals normal."
            )
        )
    }
    
    val filteredRecords = if (selectedFilter != null) {
        records.filter { it.type == selectedFilter }
    } else {
        records
    }

    BaseEnhancedScreen(
        navController = navController,
        title = "Health Records"
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search and Filter Section
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
                    // Search Bar
                    OutlinedTextField(
                        value = "",
                        onValueChange = { },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search records...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Filter Chips
                    Text(
                        text = "Filter by type:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedFilter == null,
                                onClick = { selectedFilter = null },
                                label = { Text("All") }
                            )
                        }
                        
                        items(RecordType.values()) { type ->
                            FilterChip(
                                selected = selectedFilter == type,
                                onClick = { selectedFilter = type },
                                label = { Text(type.name.replace("_", " ")) }
                            )
                        }
                    }
                }
            }
            
            // Records List
            if (filteredRecords.isEmpty()) {
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
                            Icons.Default.Folder,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No records found",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add your first health record",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredRecords.sortedByDescending { it.date }) { record ->
                        HealthRecordCard(record = record)
                    }
                }
            }
        }
        
        // FAB for adding new record
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Record")
            }
        }
    }
    
    // Add Record Dialog
    if (showAddDialog) {
        AddHealthRecordDialog(
            onDismiss = { showAddDialog = false },
            onSave = { /* Handle saving new record */ }
        )
    }
}

@Composable
fun HealthRecordCard(record: HealthRecord) {
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
                    text = record.title,
                    style = MaterialTheme.typography.titleMedium
                )
                
                // Simple Text badge for record type
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = record.type.name.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(record.date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            record.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (record.attachments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AttachFile,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${record.attachments.size} attachment(s)",
                        style = MaterialTheme.typography.bodySmall
                    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHealthRecordDialog(
    onDismiss: () -> Unit,
    onSave: (HealthRecord) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(RecordType.VACCINATION) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Health Record") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Record Type")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                var expandedType by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expandedType,
                    onExpandedChange = { expandedType = it }
                ) {
                    OutlinedTextField(
                        value = selectedType.name.replace("_", " "),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Record Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        RecordType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(text = type.name.replace("_", " ")) },
                                onClick = {
                                    selectedType = type
                                    expandedType = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Create a new record and save it
                    onSave(
                        HealthRecord(
                            id = UUID.randomUUID().toString(),
                            title = title,
                            date = Date(),
                            type = selectedType,
                            description = description
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