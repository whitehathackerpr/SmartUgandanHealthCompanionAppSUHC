package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.profile

import android.content.Intent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.AuthActivity
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.R
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SmartUgandanHealthCompanionAppSUHCTheme
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.utils.LanguageUtils
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.utils.NotificationsUtils
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.utils.ThemeUtils
import kotlinx.coroutines.launch

/**
 * Data class representing user profile information.
 */
data class UserProfile(
    val name: String,
    val age: Int,
    val gender: String,
    val bloodType: String,
    val allergies: List<String>,
    val medicalConditions: List<String>,
    val medications: List<String>
)

/**
 * Profile screen composable.
 * Allows users to view and edit their profile information and access settings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.profile),
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
        ProfileContent(
            paddingValues = paddingValues,
            navController = navController,
            onLogout = {
                // Sign out from Firebase
                FirebaseAuth.getInstance().signOut()
                
                // Navigate to AuthActivity
                val intent = Intent(context, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }
        )
    }
}

/**
 * Profile content composable.
 */
@Composable
fun ProfileContent(
    paddingValues: PaddingValues,
    navController: NavController,
    onLogout: () -> Unit
) {
    // State for user profile data
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    
    // Fetch user profile data
    LaunchedEffect(key1 = true) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Extract user profile data
                        val name = document.getString("name") ?: currentUser.displayName ?: "User"
                        val age = document.getLong("age")?.toInt() ?: 0
                        val gender = document.getString("gender") ?: "Not specified"
                        val bloodType = document.getString("bloodType") ?: "Not specified"
                        
                        // Extract lists
                        val allergies = document.get("allergies") as? List<String> ?: emptyList()
                        val medicalConditions = document.get("medicalConditions") as? List<String> ?: emptyList()
                        val medications = document.get("medications") as? List<String> ?: emptyList()
                        
                        userProfile = UserProfile(
                            name = name,
                            age = age,
                            gender = gender,
                            bloodType = bloodType,
                            allergies = allergies,
                            medicalConditions = medicalConditions,
                            medications = medications
                        )
                    } else {
                        // Create default profile if document doesn't exist
                        userProfile = UserProfile(
                            name = currentUser.displayName ?: "User",
                            age = 0,
                            gender = "Not specified",
                            bloodType = "Not specified",
                            allergies = emptyList(),
                            medicalConditions = emptyList(),
                            medications = emptyList()
                        )
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    // Create default profile on failure
                    userProfile = UserProfile(
                        name = currentUser.displayName ?: "User",
                        age = 0,
                        gender = "Not specified",
                        bloodType = "Not specified",
                        allergies = emptyList(),
                        medicalConditions = emptyList(),
                        medications = emptyList()
                    )
                    isLoading = false
                }
        } else {
            // Not logged in, use default profile
            userProfile = UserProfile(
                name = "User",
                age = 0,
                gender = "Not specified",
                bloodType = "Not specified",
                allergies = emptyList(),
                medicalConditions = emptyList(),
                medications = emptyList()
            )
            isLoading = false
        }
    }
    
    // State for dialogs
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showQrCodeDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    
    // Language, theme, and notifications utilities
    val context = LocalContext.current
    val languageUtils = remember { LanguageUtils(context) }
    val themeUtils = remember { ThemeUtils(context) }
    val notificationsUtils = remember { NotificationsUtils(context) }
    val coroutineScope = rememberCoroutineScope()
    
    // Current language state
    var currentLanguage by remember { mutableStateOf(LanguageUtils.DEFAULT_LANGUAGE) }
    
    // Current theme state
    val currentTheme by themeUtils.getAppTheme().collectAsState(initial = ThemeUtils.THEME_SYSTEM)
    
    // Current notifications state
    val notificationsEnabled by notificationsUtils.getNotificationsEnabled().collectAsState(initial = NotificationsUtils.DEFAULT_NOTIFICATIONS_ENABLED)
    
    // Collect current language
    LaunchedEffect(key1 = true) {
        languageUtils.getAppLanguage().collect { language ->
            currentLanguage = language
        }
    }
    
    // Loading state
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    // User profile should be non-null at this point
    userProfile?.let { profile ->
        
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header
        ProfileHeader(userProfile = profile)
        
        // Personal Information Section
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
                SectionHeader(
                    title = stringResource(id = R.string.personal_info),
                    icon = Icons.Default.Person,
                    actionIcon = Icons.Default.Edit,
                    onActionClick = { navController.navigate("edit_profile") }
                )
                
                Divider()
                
                // Profile details
                ProfileDetailItem(
                    label = stringResource(id = R.string.name),
                    value = profile.name
                )
                
                ProfileDetailItem(
                    label = stringResource(id = R.string.age),
                    value = if (profile.age > 0) profile.age.toString() else "Not specified"
                )
                
                ProfileDetailItem(
                    label = stringResource(id = R.string.gender),
                    value = profile.gender
                )
                
                ProfileDetailItem(
                    label = stringResource(id = R.string.blood_type),
                    value = profile.bloodType
                )
                
                // Medical information
                if (profile.allergies.isNotEmpty()) {
                    ProfileDetailItem(
                        label = stringResource(id = R.string.allergies),
                        value = profile.allergies.joinToString(", ")
                    )
                }
                
                if (profile.medicalConditions.isNotEmpty()) {
                    ProfileDetailItem(
                        label = stringResource(id = R.string.medical_conditions),
                        value = profile.medicalConditions.joinToString(", ")
                    )
                }
                
                if (profile.medications.isNotEmpty()) {
                    ProfileDetailItem(
                        label = stringResource(id = R.string.medications),
                        value = profile.medications.joinToString(", ")
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Generate Medical ID QR
                Button(
                    onClick = { showQrCodeDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(stringResource(id = R.string.generate_medical_id))
                }
            }
        }
        
        // Settings Section
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
                    text = "Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Divider()
                
                // Language setting
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = stringResource(id = R.string.language),
                    subtitle = when (currentLanguage) {
                        LanguageUtils.LANGUAGE_ENGLISH -> stringResource(id = R.string.english)
                        LanguageUtils.LANGUAGE_LUGANDA -> stringResource(id = R.string.luganda)
                        LanguageUtils.LANGUAGE_RUNYANKOLE -> stringResource(id = R.string.runyankole)
                        else -> stringResource(id = R.string.english)
                    },
                    onClick = { showLanguageDialog = true }
                )
                
                // Theme setting
                SettingsItem(
                    icon = Icons.Default.DarkMode,
                    title = stringResource(id = R.string.theme),
                    subtitle = when (currentTheme) {
                        ThemeUtils.THEME_LIGHT -> stringResource(id = R.string.light_mode)
                        ThemeUtils.THEME_DARK -> stringResource(id = R.string.dark_mode)
                        else -> stringResource(id = R.string.system_default)
                    },
                    onClick = { showThemeDialog = true }
                )
                
                // Notifications setting
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = stringResource(id = R.string.notifications),
                    subtitle = if (notificationsEnabled) "On" else "Off",
                    onClick = { showNotificationsDialog = true }
                )
                
                // Privacy setting
                SettingsItem(
                    icon = Icons.Default.Shield,
                    title = stringResource(id = R.string.privacy),
                    subtitle = "Manage privacy settings",
                    onClick = { navController.navigate("privacy_settings") }
                )
                
                Divider()
                
                // Logout button
                SettingsItem(
                    icon = Icons.Default.Logout,
                    title = stringResource(id = R.string.logout),
                    subtitle = "",
                    onClick = { showLogoutConfirmDialog = true },
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        
        // Language selection dialog
        if (showLanguageDialog) {
            LanguageSelectionDialog(
                onDismiss = { showLanguageDialog = false },
                onLanguageSelected = { languageCode ->
                    coroutineScope.launch {
                        languageUtils.setAppLanguage(languageCode)
                        showLanguageDialog = false
                    }
                },
                currentLanguage = currentLanguage
            )
        }
        
        // Theme selection dialog
        if (showThemeDialog) {
            ThemeSelectionDialog(
                onDismiss = { showThemeDialog = false },
                onThemeSelected = { theme ->
                    coroutineScope.launch {
                        themeUtils.setAppTheme(theme)
                        showThemeDialog = false
                    }
                },
                currentTheme = currentTheme
            )
        }
        
        // Logout confirmation dialog
        if (showLogoutConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutConfirmDialog = false },
                title = { Text(stringResource(id = R.string.logout)) },
                text = { Text(stringResource(id = R.string.logout_confirmation)) },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutConfirmDialog = false
                            onLogout()
                        }
                    ) {
                        Text(stringResource(id = R.string.yes))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showLogoutConfirmDialog = false }
                    ) {
                        Text(stringResource(id = R.string.no))
                    }
                }
            )
        }
        
        // QR Code dialog
        if (showQrCodeDialog) {
            QrCodeDialog(
                userProfile = profile,
                onDismiss = { showQrCodeDialog = false }
            )
        }
        
        // Notifications dialog
        if (showNotificationsDialog) {
            NotificationsDialog(
                notificationsEnabled = notificationsEnabled,
                onToggle = { enabled ->
                    coroutineScope.launch {
                        notificationsUtils.setNotificationsEnabled(enabled)
                        showNotificationsDialog = false
                    }
                },
                onDismiss = { showNotificationsDialog = false }
            )
        }
    }
    }
}

/**
 * Profile header composable.
 */
@Composable
fun ProfileHeader(userProfile: UserProfile) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userProfile.name.take(1),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // User name
        Text(
            text = userProfile.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        // User details
        val detailsText = buildString {
            if (userProfile.age > 0) {
                append("${userProfile.age} years")
                append(" • ")
            }
            append(userProfile.gender)
            append(" • ")
            append(userProfile.bloodType)
        }
        
        Text(
            text = detailsText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

/**
 * Section header composable.
 */
@Composable
fun SectionHeader(
    title: String,
    icon: ImageVector,
    actionIcon: ImageVector? = null,
    onActionClick: (() -> Unit)? = null
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        if (actionIcon != null && onActionClick != null) {
            IconButton(onClick = onActionClick) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = "Action",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Profile detail item composable.
 */
@Composable
fun ProfileDetailItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Settings item composable.
 */
@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.padding(end = 16.dp)
        )
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
        
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

/**
 * Language selection dialog composable.
 */
@Composable
fun LanguageSelectionDialog(
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    currentLanguage: String
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.language),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Language options
                LanguageOption(
                    languageName = stringResource(id = R.string.english),
                    languageCode = LanguageUtils.LANGUAGE_ENGLISH,
                    onSelect = onLanguageSelected,
                    isSelected = currentLanguage == LanguageUtils.LANGUAGE_ENGLISH
                )
                
                LanguageOption(
                    languageName = stringResource(id = R.string.luganda),
                    languageCode = LanguageUtils.LANGUAGE_LUGANDA,
                    onSelect = onLanguageSelected,
                    isSelected = currentLanguage == LanguageUtils.LANGUAGE_LUGANDA
                )
                
                LanguageOption(
                    languageName = stringResource(id = R.string.runyankole),
                    languageCode = LanguageUtils.LANGUAGE_RUNYANKOLE,
                    onSelect = onLanguageSelected,
                    isSelected = currentLanguage == LanguageUtils.LANGUAGE_RUNYANKOLE
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        }
    }
}

/**
 * Language option composable.
 */
@Composable
fun LanguageOption(
    languageName: String,
    languageCode: String,
    onSelect: (String) -> Unit,
    isSelected: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(languageCode) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelect(languageCode) }
        )
        
        Text(
            text = languageName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

/**
 * Theme selection dialog composable.
 */
@Composable
fun ThemeSelectionDialog(
    onDismiss: () -> Unit,
    onThemeSelected: (String) -> Unit,
    currentTheme: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.theme),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                // Theme options
                ThemeOption(
                    themeName = stringResource(id = R.string.light_mode),
                    themeValue = "light",
                    onSelect = onThemeSelected,
                    isSelected = currentTheme == "light"
                )
                
                ThemeOption(
                    themeName = stringResource(id = R.string.dark_mode),
                    themeValue = "dark",
                    onSelect = onThemeSelected,
                    isSelected = currentTheme == "dark"
                )
                
                ThemeOption(
                    themeName = stringResource(id = R.string.system_default),
                    themeValue = "system",
                    onSelect = onThemeSelected,
                    isSelected = currentTheme == "system"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}

/**
 * Theme option composable.
 */
@Composable
fun ThemeOption(
    themeName: String,
    themeValue: String,
    onSelect: (String) -> Unit,
    isSelected: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(themeValue) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelect(themeValue) }
        )
        
        Text(
            text = themeName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

/**
 * Notifications dialog composable.
 */
@Composable
fun NotificationsDialog(
    notificationsEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.notifications),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Receive important health reminders and alerts from the app.",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enable Notifications",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    androidx.compose.material3.Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { onToggle(it) }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Close")
            }
        }
    )
}

/**
 * QR Code dialog composable.
 */
@Composable
fun QrCodeDialog(
    userProfile: UserProfile,
    onDismiss: () -> Unit
) {
    // Generate QR code content
    val qrContent = buildString {
        append("MEDICAL ID\n")
        append("Name: ${userProfile.name}\n")
        if (userProfile.age > 0) {
            append("Age: ${userProfile.age}\n")
        }
        append("Gender: ${userProfile.gender}\n")
        append("Blood Type: ${userProfile.bloodType}\n")
        
        if (userProfile.allergies.isNotEmpty()) {
            append("Allergies: ${userProfile.allergies.joinToString(", ")}\n")
        }
        
        if (userProfile.medicalConditions.isNotEmpty()) {
            append("Medical Conditions: ${userProfile.medicalConditions.joinToString(", ")}\n")
        }
        
        if (userProfile.medications.isNotEmpty()) {
            append("Medications: ${userProfile.medications.joinToString(", ")}\n")
        }
        
        append("Generated: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}")
    }
    
    // Generate QR code bitmap
    val qrCodeBitmap = remember(qrContent) {
        try {
            val qrCodeWriter = QRCodeWriter()
            val bitMatrix = qrCodeWriter.encode(
                qrContent,
                BarcodeFormat.QR_CODE,
                512,
                512
            )
            
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.RGB_565)
            
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.Black.hashCode() else Color.White.hashCode())
                }
            }
            
            bitmap
        } catch (e: Exception) {
            null
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Medical ID",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                if (qrCodeBitmap != null) {
                    Image(
                        bitmap = qrCodeBitmap.asImageBitmap(),
                        contentDescription = "Medical ID QR Code",
                        modifier = Modifier
                            .size(250.dp)
                            .padding(8.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(250.dp)
                            .background(MaterialTheme.colorScheme.errorContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error generating QR code",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Text(
                    text = "Scan this QR code to quickly access the patient's medical information in case of emergency.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                Text("Close")
                }
            }
        }
    }
}

/**
 * Preview function for the profile screen.
 */
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    SmartUgandanHealthCompanionAppSUHCTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProfileScreen(rememberNavController())
        }
    }
}