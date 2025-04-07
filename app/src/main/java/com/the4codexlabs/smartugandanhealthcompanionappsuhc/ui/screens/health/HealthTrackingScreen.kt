package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.health

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.R
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SmartUgandanHealthCompanionAppSUHCTheme
import java.util.Date

/**
 * Health Tracking screen composable.
 * Allows users to input and track their health metrics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthTrackingScreen(navController: NavController) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.track_vitals),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        HealthTrackingContent(
            paddingValues = paddingValues,
            onSaveClick = { healthData ->
                saveHealthData(healthData, context) { success ->
                    if (success) {
                        Toast.makeText(context, context.getString(R.string.data_saved_successfully), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, context.getString(R.string.error_saving_data), Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onViewChartsClick = {
                // Navigate to charts screen
                // For now, just show a toast
                Toast.makeText(context, context.getString(R.string.charts_coming_soon), Toast.LENGTH_SHORT).show()
            },
            onExportPdfClick = {
                // Generate and export PDF report
                // For now, just show a toast
                Toast.makeText(context, context.getString(R.string.pdf_export_coming_soon), Toast.LENGTH_SHORT).show()
            }
        )
    }
}

/**
 * Health tracking content composable.
 */
@Composable
fun HealthTrackingContent(
    paddingValues: PaddingValues,
    onSaveClick: (HealthData) -> Unit,
    onViewChartsClick: () -> Unit,
    onExportPdfClick: () -> Unit
) {
    // State for health metrics
    var systolic by remember { mutableStateOf("") }
    var diastolic by remember { mutableStateOf("") }
    var bloodSugar by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var moodIndex by remember { mutableIntStateOf(2) } // Default to "Okay"
    var waterIntake by remember { mutableFloatStateOf(4f) } // Default to 4 glasses
    
    // Get string resources outside of non-composable contexts
    val errorBloodPressureRequired = stringResource(id = R.string.error_blood_pressure_required)
    val errorBloodSugarRequired = stringResource(id = R.string.error_blood_sugar_required)
    val errorWeightRequired = stringResource(id = R.string.error_weight_required)
    
    // Validation state
    var showValidationError by remember { mutableStateOf(false) }
    var validationErrorMessage by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Blood Pressure Section
        HealthMetricCard(
            title = stringResource(id = R.string.blood_pressure)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Systolic
                OutlinedTextField(
                    value = systolic,
                    onValueChange = { systolic = it },
                    label = { Text(stringResource(id = R.string.systolic)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                
                // Diastolic
                OutlinedTextField(
                    value = diastolic,
                    onValueChange = { diastolic = it },
                    label = { Text(stringResource(id = R.string.diastolic)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            
            Text(
                text = stringResource(id = R.string.mmHg),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }
        
        // Blood Sugar Section
        HealthMetricCard(
            title = stringResource(id = R.string.blood_sugar)
        ) {
            OutlinedTextField(
                value = bloodSugar,
                onValueChange = { bloodSugar = it },
                label = { Text(stringResource(id = R.string.blood_sugar)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Text(
                text = stringResource(id = R.string.mg_dl),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }
        
        // Weight Section
        HealthMetricCard(
            title = stringResource(id = R.string.weight)
        ) {
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text(stringResource(id = R.string.weight)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Text(
                text = stringResource(id = R.string.kg),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }
        
        // Mood Section
        HealthMetricCard(
            title = stringResource(id = R.string.mood)
        ) {
            val options = listOf(
                stringResource(id = R.string.mood_terrible),
                stringResource(id = R.string.mood_bad),
                stringResource(id = R.string.mood_okay),
                stringResource(id = R.string.mood_good),
                stringResource(id = R.string.mood_great)
            )
            
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        selected = index == moodIndex,
                        onClick = { moodIndex = index },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(label, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        
        // Water Intake Section
        HealthMetricCard(
            title = stringResource(id = R.string.water_intake)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "0",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Slider(
                    value = waterIntake,
                    onValueChange = { waterIntake = it },
                    valueRange = 0f..12f,
                    steps = 11,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = "12",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Text(
                text = "${waterIntake.toInt()} ${stringResource(id = R.string.glasses)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        
        // Validation error message
        if (showValidationError) {
            Text(
                text = validationErrorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        
        // Action Buttons
        Button(
            onClick = {
                // Validate inputs
                when {
                    systolic.isBlank() || diastolic.isBlank() -> {
                        showValidationError = true
                        validationErrorMessage = errorBloodPressureRequired
                    }
                    bloodSugar.isBlank() -> {
                        showValidationError = true
                        validationErrorMessage = errorBloodSugarRequired
                    }
                    weight.isBlank() -> {
                        showValidationError = true
                        validationErrorMessage = errorWeightRequired
                    }
                    else -> {
                        showValidationError = false
                        // Create health data object
                        val healthData = HealthData(
                            systolic = systolic.toIntOrNull() ?: 0,
                            diastolic = diastolic.toIntOrNull() ?: 0,
                            bloodSugar = bloodSugar.toFloatOrNull() ?: 0f,
                            weight = weight.toFloatOrNull() ?: 0f,
                            mood = moodIndex,
                            waterIntake = waterIntake.toInt(),
                            timestamp = Date()
                        )
                        onSaveClick(healthData)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.save))
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalButton(
                onClick = onViewChartsClick,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(stringResource(id = R.string.view_charts))
            }
            
            FilledTonalButton(
                onClick = onExportPdfClick,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(stringResource(id = R.string.export_pdf))
            }
        }
    }
}

/**
 * Data class representing health metrics.
 */
data class HealthData(
    val systolic: Int,
    val diastolic: Int,
    val bloodSugar: Float,
    val weight: Float,
    val mood: Int,
    val waterIntake: Int,
    val timestamp: Date
)

/**
 * Save health data to Firebase Firestore.
 */
private fun saveHealthData(healthData: HealthData, context: Context, callback: (Boolean) -> Unit) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    
    if (currentUser == null) {
        callback(false)
        return
    }
    
    val db = FirebaseFirestore.getInstance()
    val healthDataMap = hashMapOf(
        "systolic" to healthData.systolic,
        "diastolic" to healthData.diastolic,
        "bloodSugar" to healthData.bloodSugar,
        "weight" to healthData.weight,
        "mood" to healthData.mood,
        "waterIntake" to healthData.waterIntake,
        "timestamp" to healthData.timestamp
    )
    
    db.collection("users")
        .document(currentUser.uid)
        .collection("health_metrics")
        .add(healthDataMap)
        .addOnSuccessListener {
            callback(true)
        }
        .addOnFailureListener {
            callback(false)
        }
}

/**
 * Health metric card composable.
 */
@Composable
fun HealthMetricCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            content()
        }
    }
}

/**
 * Preview function for the health tracking screen.
 */
@Preview(showBackground = true)
@Composable
fun HealthTrackingScreenPreview() {
    SmartUgandanHealthCompanionAppSUHCTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HealthTrackingScreen(rememberNavController())
        }
    }
}