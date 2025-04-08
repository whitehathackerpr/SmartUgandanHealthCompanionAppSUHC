package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.dashboard

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.R
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.MedicationReminderViewModel
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.TimeOfDay
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SmartUgandanHealthCompanionAppSUHCTheme
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.AppLanguage
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.LanguageProvider
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.StringResources
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.getTranslation
import java.text.SimpleDateFormat
import java.util.*

data class FeatureItem(
    val title: Map<AppLanguage, String>,
    val icon: ImageVector,
    val route: String,
    val color: Color
)

@Composable
fun DashboardScreen(
    onFeatureClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    medicationViewModel: MedicationReminderViewModel = viewModel()
) {
    val medicationUiState by medicationViewModel.uiState.collectAsState()
    val nextDose = medicationViewModel.getNextDoseTime()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidth = configuration.screenWidthDp
    
    val isTablet = screenWidth >= 600
    val userRepository = remember { UserRepository() }
    val sosRepository = remember { SOSRepository() }
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var showSOSDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf<String?>(null) }
    var isTriggeringSOS by remember { mutableStateOf(false) }

    var showWelcomeAnimation by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        showWelcomeAnimation = true
        userRepository.getCurrentUser().collect { profile ->
            userProfile = profile
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Space for bottom nav bar
        ) {
            // Welcome section with user profile
            item {
                WelcomeHeader(
                    onNotificationsClick = onNotificationsClick,
                    onProfileClick = onProfileClick,
                    showAnimation = showWelcomeAnimation,
                    userProfile = userProfile
                )
            }
            
            // SOS Button
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { showSOSDialog = true },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "SOS",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "SOS Emergency",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            // Stats overview
            item {
                StatsOverview(
                    nextDose = nextDose,
                    todaysMedications = medicationUiState.todayMedications.size,
                    userProfile = userProfile
                )
            }
            
            // Quick access features
            item {
                Text(
                    text = getTranslation(StringResources.appName),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 8.dp)
                )
                
                if (isTablet || isLandscape) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.height(if (isLandscape) 270.dp else 400.dp)
                    ) {
                        items(features) { feature ->
                            FeatureCard(
                                feature = feature,
                                onClick = { onFeatureClick(feature.route) }
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val featurePairs = features.chunked(2)
                        featurePairs.forEach { rowFeatures ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                rowFeatures.forEach { feature ->
                                    FeatureCard(
                                        feature = feature,
                                        onClick = { onFeatureClick(feature.route) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (rowFeatures.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
            
            // Upcoming medications
            item {
                UpcomingMedications(
                    medications = medicationUiState.todayMedications,
                    onViewAllClick = { onFeatureClick("medication_reminder") }
                )
            }
            
            // Health tips
            item {
                HealthTips(
                    onViewMoreClick = { onFeatureClick("health_education") }
                )
            }
        }
        
        // SOS Dialog
        if (showSOSDialog) {
            AlertDialog(
                onDismissRequest = { showSOSDialog = false },
                title = { Text("Emergency SOS") },
                text = { 
                    Column {
                        Text("Are you sure you want to trigger the emergency SOS? This will alert your emergency contacts and nearby healthcare providers.")
                        if (isTriggeringSOS) {
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            isTriggeringSOS = true
                            // TODO: Get actual location from location services
                            val dummyLocation = mapOf(
                                "latitude" to 0.0,
                                "longitude" to 0.0
                            )
                            
                            // Trigger SOS
                            sosRepository.triggerSOS(
                                location = dummyLocation,
                                description = "Emergency SOS triggered by user"
                            ).onSuccess {
                                showSOSDialog = false
                            }.onFailure { error ->
                                showErrorDialog = error.message ?: "Failed to trigger SOS"
                            }
                            isTriggeringSOS = false
                        },
                        enabled = !isTriggeringSOS,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Trigger SOS")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showSOSDialog = false },
                        enabled = !isTriggeringSOS
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // Error Dialog
        showErrorDialog?.let { error ->
            AlertDialog(
                onDismissRequest = { showErrorDialog = null },
                title = { Text("Error") },
                text = { Text(error) },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = null }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun WelcomeHeader(
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    showAnimation: Boolean,
    userProfile: UserProfile?
) {
    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> getTranslation(StringResources.good_morning)
        in 12..16 -> getTranslation(StringResources.good_afternoon)
        else -> getTranslation(StringResources.good_evening)
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getTranslation(StringResources.dashboard),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
                
                Row {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    
                    IconButton(onClick = onProfileClick) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                        ) {
                            if (userProfile?.photoUrl?.isNotEmpty() == true) {
                                // TODO: Load profile image from URL
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(4.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = greeting,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = userProfile?.name ?: getTranslation(StringResources.guest),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatsOverview(
    nextDose: TimeOfDay?,
    todaysMedications: Int,
    userProfile: UserProfile?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Next medication
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Medication,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Next Dose",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = nextDose?.formatTime() ?: "No upcoming doses",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Today's medications
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Today's Medications",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "$todaysMedications medications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FeatureCard(
    feature: FeatureItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = feature.color.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = feature.icon,
                        contentDescription = null,
                        tint = feature.color,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = getTranslation(feature.title),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun UpcomingMedications(
    medications: List<com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.Medication>,
    onViewAllClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Upcoming Medications",
                style = MaterialTheme.typography.titleMedium
            )
            
            TextButton(onClick = onViewAllClick) {
                Text("View All")
            }
        }
        
        if (medications.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "No medications for today",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Add medications to get reminders",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(medications.take(5)) { medication ->
                    MedicationReminderCard(medication = medication)
                }
            }
        }
    }
}

@Composable
fun MedicationReminderCard(
    medication: com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.Medication
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(160.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.Medication,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = medication.dosage,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = medication.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            if (medication.times.isNotEmpty()) {
                Text(
                    text = medication.times.joinToString(", ") { it.formatTime() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            medication.notes?.let {
                if (it.isNotEmpty()) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun HealthTips(
    onViewMoreClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Health Tips",
                style = MaterialTheme.typography.titleMedium
            )
            
            TextButton(onClick = onViewMoreClick) {
                Text("View More")
            }
        }
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(24.dp)
                )
                
                Column {
                    Text(
                        text = "Drink at least 8 glasses of water daily",
                        style = MaterialTheme.typography.titleSmall
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Staying hydrated helps maintain your body's fluid balance, aids digestion, and keeps your skin healthy.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.DirectionsRun,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(24.dp)
                )
                
                Column {
                    Text(
                        text = "Exercise for 30 minutes most days",
                        style = MaterialTheme.typography.titleSmall
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Regular exercise can help reduce the risk of chronic diseases, improve your mood, and enhance your overall quality of life.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

// List of features for the dashboard
val features = listOf(
    FeatureItem(
        title = StringResources.healthRecords,
        icon = Icons.Outlined.Folder,
        route = "health_records",
        color = Color(0xFF4A6572)
    ),
    FeatureItem(
        title = StringResources.medicationReminders,
        icon = Icons.Outlined.Medication,
        route = "medication_reminder",
        color = Color(0xFF1CADE4)
    ),
    FeatureItem(
        title = StringResources.symptomTracker,
        icon = Icons.Outlined.Analytics,
        route = "symptom_tracker",
        color = Color(0xFFBD4F6C)
    ),
    FeatureItem(
        title = StringResources.healthEducation,
        icon = Icons.Outlined.MenuBook,
        route = "health_education",
        color = Color(0xFF019267)
    ),
    FeatureItem(
        title = StringResources.community,
        icon = Icons.Outlined.Group,
        route = "community",
        color = Color(0xFFE29578)
    ),
    FeatureItem(
        title = StringResources.wearables,
        icon = Icons.Outlined.Watch,
        route = "wearables",
        color = Color(0xFF7D3AC1)
    )
)

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DashboardScreenPreview() {
    SmartUgandanHealthCompanionAppSUHCTheme {
        DashboardScreen(
            onFeatureClick = {},
            onNotificationsClick = {},
            onProfileClick = {}
        )
    }
} 