package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.R
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.Screen
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SmartUgandanHealthCompanionAppSUHCTheme
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SOSRed

/**
 * Dashboard screen composable.
 * This is the main screen of the app, showing a summary of health data and quick actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        DashboardContent(
            paddingValues = paddingValues,
            onHealthTrackingClick = { navController.navigate(Screen.HealthTracking.route) },
            onDiagnosisClick = { navController.navigate(Screen.Diagnosis.route) },
            onSOSClick = { navController.navigate(Screen.SOS.route) },
            onReminderClick = { /* TODO: Implement reminder dialog */ }
        )
    }
}

/**
 * Dashboard content composable.
 */
@Composable
fun DashboardContent(
    paddingValues: PaddingValues,
    onHealthTrackingClick: () -> Unit,
    onDiagnosisClick: () -> Unit,
    onSOSClick: () -> Unit,
    onReminderClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome section
        WelcomeSection()
        
        // Health summary section
        HealthSummarySection()
        
        // Quick actions section
        QuickActionsSection(
            onHealthTrackingClick = onHealthTrackingClick,
            onDiagnosisClick = onDiagnosisClick,
            onSOSClick = onSOSClick,
            onReminderClick = onReminderClick
        )
        
        // Upcoming reminders section
        UpcomingRemindersSection()
    }
}

/**
 * Welcome section composable.
 */
@Composable
fun WelcomeSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.welcome),
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Get current user name from Firebase
            val userName = remember { mutableStateOf("Loading...") }
            
            // Fetch user data from Firestore
            LaunchedEffect(key1 = true) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                currentUser?.let { user ->
                    // Try to get display name first
                    if (!user.displayName.isNullOrEmpty()) {
                        userName.value = user.displayName!!
                    } else {
                        // If no display name, fetch from Firestore
                        FirebaseFirestore.getInstance().collection("users")
                            .document(user.uid)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document != null && document.exists()) {
                                    val name = document.getString("name")
                                    if (!name.isNullOrEmpty()) {
                                        userName.value = name
                                    } else {
                                        userName.value = "User"
                                    }
                                } else {
                                    userName.value = "User"
                                }
                            }
                            .addOnFailureListener {
                                userName.value = "User"
                            }
                    }
                }
            }
            
            Text(
                text = userName.value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stringResource(id = R.string.app_tagline),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Health summary section composable.
 */
@Composable
fun HealthSummarySection() {
    // Get string resources outside of LaunchedEffect
    val mmHgUnit = stringResource(id = R.string.mmHg)
    val mgDlUnit = stringResource(id = R.string.mg_dl)
    val kgUnit = stringResource(id = R.string.kg)
    val glassesUnit = stringResource(id = R.string.glasses)
    
    // State for health metrics
    val bloodPressure = remember { mutableStateOf("--/-- $mmHgUnit") }
    val bloodSugar = remember { mutableStateOf("-- $mgDlUnit") }
    val weight = remember { mutableStateOf("-- $kgUnit") }
    val waterIntake = remember { mutableStateOf("-- $glassesUnit") }
    
    // Fetch health metrics from Firestore
    LaunchedEffect(key1 = true) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            // Get the latest health metrics
            FirebaseFirestore.getInstance().collection("users")
                .document(user.uid)
                .collection("health_metrics")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val latestMetrics = documents.documents[0]
                        
                        // Blood pressure
                        val systolic = latestMetrics.getLong("systolic")
                        val diastolic = latestMetrics.getLong("diastolic")
                        if (systolic != null && diastolic != null) {
                            bloodPressure.value = "$systolic/$diastolic $mmHgUnit"
                        }
                        
                        // Blood sugar
                        val sugar = latestMetrics.getLong("blood_sugar")
                        if (sugar != null) {
                            bloodSugar.value = "$sugar $mgDlUnit"
                        }
                        
                        // Weight
                        val userWeight = latestMetrics.getDouble("weight")
                        if (userWeight != null) {
                            weight.value = "$userWeight $kgUnit"
                        }
                        
                        // Water intake
                        val water = latestMetrics.getLong("water_intake")
                        if (water != null) {
                            waterIntake.value = "$water $glassesUnit"
                        }
                    }
                }
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.health_summary),
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Blood pressure
            HealthMetricItem(
                label = stringResource(id = R.string.blood_pressure),
                value = bloodPressure.value,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Blood sugar
            HealthMetricItem(
                label = stringResource(id = R.string.blood_sugar),
                value = bloodSugar.value,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Weight
            HealthMetricItem(
                label = stringResource(id = R.string.weight),
                value = weight.value,
                color = MaterialTheme.colorScheme.tertiary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Water intake
            HealthMetricItem(
                label = stringResource(id = R.string.water_intake),
                value = waterIntake.value,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Health metric item composable.
 */
@Composable
fun HealthMetricItem(
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Quick actions section composable.
 */
@Composable
fun QuickActionsSection(
    onHealthTrackingClick: () -> Unit,
    onDiagnosisClick: () -> Unit,
    onSOSClick: () -> Unit,
    onReminderClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.quick_actions),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Track Health
            QuickActionItem(
                icon = Icons.Default.Favorite,
                label = stringResource(id = R.string.track_health_now),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
                onClick = onHealthTrackingClick
            )
            
            // Check Symptoms
            QuickActionItem(
                icon = Icons.Default.HealthAndSafety,
                label = stringResource(id = R.string.check_symptoms),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f),
                onClick = onDiagnosisClick
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // SOS
            QuickActionItem(
                icon = Icons.Default.Warning,
                label = stringResource(id = R.string.emergency_sos),
                color = SOSRed,
                modifier = Modifier.weight(1f),
                onClick = onSOSClick
            )
            
            // Set Reminder
            QuickActionItem(
                icon = Icons.Default.Notifications,
                label = stringResource(id = R.string.set_reminder),
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f),
                onClick = onReminderClick
            )
        }
    }
}

/**
 * Quick action item composable.
 */
@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f),
            contentColor = color
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Data class for reminders
 */
data class Reminder(
    val title: String,
    val time: String
)

/**
 * Upcoming reminders section composable.
 */
@Composable
fun UpcomingRemindersSection() {
    // State for reminders
    val reminders = remember { mutableStateOf<List<Reminder>>(emptyList()) }
    
    // Fetch reminders from Firestore
    LaunchedEffect(key1 = true) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            // Get upcoming reminders
            FirebaseFirestore.getInstance().collection("users")
                .document(user.uid)
                .collection("reminders")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .whereGreaterThan("timestamp", com.google.firebase.Timestamp.now())
                .limit(5)
                .get()
                .addOnSuccessListener { documents ->
                    val remindersList = mutableListOf<Reminder>()
                    
                    for (document in documents) {
                        val title = document.getString("title") ?: continue
                        val timestamp = document.getTimestamp("timestamp")
                        
                        if (timestamp != null) {
                            val date = timestamp.toDate()
                            val calendar = java.util.Calendar.getInstance()
                            calendar.time = date
                            
                            val timeString = when {
                                // Today
                                android.text.format.DateUtils.isToday(date.time) -> {
                                    "Today, ${calendar.get(java.util.Calendar.HOUR)}:${String.format("%02d", calendar.get(java.util.Calendar.MINUTE))} ${if (calendar.get(java.util.Calendar.AM_PM) == java.util.Calendar.AM) "AM" else "PM"}"
                                }
                                // Tomorrow
                                isDateTomorrow(date) -> {
                                    "Tomorrow, ${calendar.get(java.util.Calendar.HOUR)}:${String.format("%02d", calendar.get(java.util.Calendar.MINUTE))} ${if (calendar.get(java.util.Calendar.AM_PM) == java.util.Calendar.AM) "AM" else "PM"}"
                                }
                                // Other days
                                else -> {
                                    val formatter = java.text.SimpleDateFormat("MMM dd, h:mm a", java.util.Locale.getDefault())
                                    formatter.format(date)
                                }
                            }
                            
                            remindersList.add(Reminder(title, timeString))
                        }
                    }
                    
                    reminders.value = remindersList
                }
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.upcoming_reminders),
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (reminders.value.isEmpty()) {
                Text(
                    text = "No upcoming reminders",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                reminders.value.forEachIndexed { index, reminder ->
                    ReminderItem(
                        title = reminder.title,
                        time = reminder.time
                    )
                    
                    if (index < reminders.value.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

/**
 * Helper function to check if a date is tomorrow.
 */
private fun isDateTomorrow(date: java.util.Date): Boolean {
    val tomorrow = java.util.Calendar.getInstance()
    tomorrow.add(java.util.Calendar.DAY_OF_YEAR, 1)
    tomorrow.set(java.util.Calendar.HOUR_OF_DAY, 0)
    tomorrow.set(java.util.Calendar.MINUTE, 0)
    tomorrow.set(java.util.Calendar.SECOND, 0)
    tomorrow.set(java.util.Calendar.MILLISECOND, 0)
    
    val dayAfterTomorrow = java.util.Calendar.getInstance()
    dayAfterTomorrow.add(java.util.Calendar.DAY_OF_YEAR, 2)
    dayAfterTomorrow.set(java.util.Calendar.HOUR_OF_DAY, 0)
    dayAfterTomorrow.set(java.util.Calendar.MINUTE, 0)
    dayAfterTomorrow.set(java.util.Calendar.SECOND, 0)
    dayAfterTomorrow.set(java.util.Calendar.MILLISECOND, 0)
    
    return date.after(tomorrow.time) && date.before(dayAfterTomorrow.time)
}
/**
 * Reminder item composable.
 */
@Composable
fun ReminderItem(
    title: String,
    time: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
        
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
        )
    }
}

/**
 * Preview function for the dashboard screen.
 */
@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    SmartUgandanHealthCompanionAppSUHCTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DashboardScreen(rememberNavController())
        }
    }
}